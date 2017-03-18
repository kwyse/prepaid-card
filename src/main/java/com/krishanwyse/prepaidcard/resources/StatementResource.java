package com.krishanwyse.prepaidcard.resources;

import com.krishanwyse.prepaidcard.core.StatementEntry;
import com.krishanwyse.prepaidcard.db.StatementDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/statements")
@Produces(MediaType.APPLICATION_JSON)
public class StatementResource {
    private final StatementDao dao;

    public StatementResource(StatementDao dao) {
        this.dao = dao;
    }

    @GET
    @Path("/{id}")
    public List<StatementEntry> getAllForId(@PathParam("id") long id) {
        return dao.selectByCardId(id);
    }
}
