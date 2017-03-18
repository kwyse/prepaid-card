package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.*;
import com.krishanwyse.prepaidcard.db.MerchantDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/merchants")
@Produces(MediaType.APPLICATION_JSON)
public class MerchantResource {
    private final MerchantDao merchantDao;

    public MerchantResource(MerchantDao merchantDao) {
        this.merchantDao = merchantDao;
    }

    @GET
    public List<Merchant> getAll() {
        return merchantDao.selectAll();
    }

    @GET
    @Path("/{id}")
    public Merchant get(@PathParam("id") long id) {
        return getMerchantIfExists(id);
    }

    @PUT
    @Path("/{id}")
    public synchronized Merchant updateBalance(@PathParam("id") long id, Amount amount) {
        Merchant merchant = getMerchantIfExists(id);
        double balance = getNewBalanceIfValid(merchant, amount);

        merchantDao.updateBalance(id, balance);
        return merchantDao.selectById(id);
    }

    private Merchant getMerchantIfExists(long id) {
        Merchant merchant = merchantDao.selectById(id);
        if (merchant == null)
            throw new BadRequestException(String.format("Merchant with ID %d not found", id));

        return merchant;
    }

    private double getNewBalanceIfValid(Merchant merchant, Amount amount) {
        double balance = merchant.getBalance();
        double reduction = amount.get();
        if (reduction > balance) {
            String message = String.format("Amount £%.2f is greater than £%.2f available", reduction, balance);
            throw new BadRequestException(message);
        } else if (reduction < 0)
            throw new BadRequestException("Amount must be positive");

        return balance - reduction;
    }
}
