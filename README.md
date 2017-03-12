[![Build Status](https://travis-ci.org/kwyse/prepaid-card.svg?branch=master)](https://travis-ci.org/kwyse/prepaid-card)

# A Simple Prepaid Card API

Provides a REST API for common prepaid card operations, such as loading
money onto a card and merchants authorizing payment. All data is stored in
an in-memory SQLite database. Built with Dropwizard.

You can play around with it [here](https://prepaid-card.herokuapp.com/cards).
Heroku [doesn't play well](https://devcenter.heroku.com/articles/sqlite3) with
SQLite, so you will likely see some strange things (creating cards and seeing
the ROWID increase but seeing no cards on the root GET request). But is still
serves as a demonstration. :grin:

## Future Improvements

* Moving to an on-disk database (probably PostgreSQL becaues of Heroku)
* Actually transferring the money to the merchants
