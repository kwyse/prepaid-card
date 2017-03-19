package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.*;
import com.krishanwyse.prepaidcard.db.CardDao;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/cards")
@Produces(MediaType.APPLICATION_JSON)
public class CardResource {
    private final CardDao dao;

    public CardResource(CardDao dao) {
        this.dao = dao;
    }

    @GET
    public List<Card> getAll() {
        return dao.selectAll();
    }

    @GET
    @Path("/{id}")
    public Card get(@PathParam("id") long id) {
        return getCardIfExists(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Card add(@Valid Card card) {
        long id = dao.insert(card);
        card.setId(id);
        return card;
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Card load(@PathParam("id") long id, Amount amount) {
        if (amount.get() < 0)
            throw new BadRequestException("Only a positive amount can be loaded onto this card");

        Card card = getCardIfExists(id);
        double newBalance = card.getBalance() + amount.get();

        dao.updateBalance(id, newBalance);
        card.setBalance(newBalance);
        return card;
    }

    private Card getCardIfExists(long id) {
        Card card = dao.selectById(id);
        if (card == null)
            throw new BadRequestException(String.format("Card with ID %d not found", id));

        return card;
    }
}
