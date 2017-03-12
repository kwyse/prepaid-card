[![Build Status](https://travis-ci.org/kwyse/prepaid-card.svg?branch=master)](https://travis-ci.org/kwyse/prepaid-card)

# A Simple Prepaid Card API

Provides a REST API for common prepaid card operations, such as loading
money onto a card and merchants authorizing payment. All data is stored in
an in-memory SQLite database. You can play around with it
[here](https://prepaid-card.herokuapp.com/cards). It has some bugs and the
deployed version does not necessarily match this repo, but it serves as a
demonstration. :grin:

## Future Improvements

* Adding a statements endpoint
* Moving to an on-disk database
* Actually transferring the money to the merchants
