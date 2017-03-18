package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.*;
import com.krishanwyse.prepaidcard.db.CardDao;
import com.krishanwyse.prepaidcard.db.TransactionDao;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/cards")
@Produces(MediaType.APPLICATION_JSON)
public class CardResource {
    private final CardDao cardDao;
    private final TransactionDao transactionDao;

    public CardResource(CardDao cardDao, TransactionDao transactionDao) {
        this.cardDao = cardDao;
        this.transactionDao = transactionDao;
    }

    @GET
    public List<Card> getAll() {
        return cardDao.selectAll();
    }

    @GET
    @Path("/{id}")
    public Card get(@PathParam("id") long id) {
        return getCardIfExists(id);
    }

    @POST
    public Card add(@Valid Card card) {
        long id = cardDao.insert(card);
        card.setId(id);
        return card;
    }

    @PUT
    @Path("/{id}")
    public Card load(@PathParam("id") long id, Amount amount) {
        if (amount.get() < 0)
            throw new BadRequestException("Only a positive amount can be loaded onto this card");

        Card card = getCardIfExists(id);
        double newBalance = card.getBalance() + amount.get();

        cardDao.update(id, newBalance);
        card.setBalance(newBalance);
        return card;
    }

    @POST
    @Path("/{cardId}/transactions")
    public Transaction addTransaction(@PathParam("cardId") long cardId, @Valid Transaction transaction) {
        Card card = getCardIfExists(cardId);
        transaction.setCard(cardId);

        if (transaction.getRemaining() > card.getBalance()) {
            String message = String.format(
                    "Insufficient funds on card, £%.2f requested but £%.2f available",
                    transaction.getRemaining(),
                    card.getBalance()
            );
            throw new BadRequestException(message);
        }

        cardDao.update(cardId,card.getBalance() - transaction.getRemaining());

        long transactionId = transactionDao.add(transaction);
        transaction.setId(transactionId);
        return transaction;
    }

    @PUT
    @Path("/{cardId}/transactions/{transactionId}")
    public Transaction update(@PathParam("cardId") long cardId,
                              @PathParam("transactionId") long transactionId,
                              @Valid Update update) {
        Transaction transaction = getTransactionIfValid(cardId, transactionId, update);

        // FIXME: Reverse and refund look at the wrong original amounts
        switch (update.getType()) {
            case CAPTURE:
                return capture(transaction, update.getAmount());
            case REVERSE:
                return reverse(transaction, update.getAmount());
            case REFUND:
                return refund(transaction, update.getAmount());
            default:
                // TODO: This doesn't execute, error is instead "Unable to process JSON"
                throw new BadRequestException(String.format("Unrecognised update type %s", update.getType()));
        }
    }

    private Card getCardIfExists(long id) {
        Card card = cardDao.selectById(id);
        if (card == null)
            throw new BadRequestException(String.format("Card with ID %d not found", id));

        return card;
    }

    private Transaction getTransactionIfValid(long cardId, long transactionId, Update update) {
        if (update.getType() == null)
            throw new BadRequestException("Update type must be specified");

        Transaction transaction = transactionDao.findById(transactionId);
        if (transaction == null)
            throw new BadRequestException(String.format("Transaction with ID %d not found", transactionId));

        if (transaction.getCard() != cardId) {
            String message = String.format(
                    "URL card ID %d does not match transaction card ID %d",
                    cardId,
                    transaction.getCard()
            );
            throw new BadRequestException(message);
        }

        return transaction;
    }

    private Transaction capture(Transaction transaction, double amount) {
        transaction = reduceRemainingAmountIfAvailable(transaction, amount);
        transactionDao.update(transaction.getId(), transaction.getRemaining(), amount);

        transaction.setCaptured(amount);
        return transaction;
    }

    private Transaction reverse(Transaction transaction, double amount) {
        transaction = reduceRemainingAmountIfAvailable(transaction, amount);
        transactionDao.update(transaction.getId(), transaction.getRemaining(), transaction.getCaptured());

        return transaction;
    }

    private Transaction refund(Transaction transaction, double amount) {
        if (amount > transaction.getCaptured()) {
            String message = String.format(
                    "Insufficient amount captured, £%.2f requested for refund but £%.2f available",
                    amount,
                    transaction.getCaptured()
            );
            throw new BadRequestException(message);
        }

        double newCapturedAmount = transaction.getCaptured() - amount;
        transactionDao.update(transaction.getId(), transaction.getRemaining(), newCapturedAmount);

        Card card = cardDao.selectById(transaction.getCard());
        double newBalance = card.getBalance() + amount;
        cardDao.update(transaction.getCard(), newBalance);

        transaction.setCaptured(newCapturedAmount);
        return transaction;
    }

    private Transaction reduceRemainingAmountIfAvailable(Transaction transaction, double amount) {
        if (amount > transaction.getRemaining()) {
            String message = String.format(
                    "Insufficient authorized amount remaining, £%.2f requested but £%.2f available",
                    amount,
                    transaction.getRemaining()
            );
            throw new BadRequestException(message);
        }

        double newRemainingAmount = transaction.getRemaining() - amount;
        transaction.setRemaining(newRemainingAmount);

        return transaction;
    }
}
