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

## Usage

The Goron Elder wishes to acquire a new card. He sends a `POST` request to
`/cards` along the lines of:

```json
{
  "name": "Goron Elder",
  "amount": 9999
}
```

During his visit to Clock Town, he stops by the Milk Bar and has some Chateau
Romani for `199.95` (he gets a discount, being an elder and all). Mr. Barten
sends the following `POST` request:

```json
{
  "card": 1,
  "merchant": 1,
  "remaining": 199.95,
  "captured": 0
}
```

The Goron Elder wants to check his balance. He can do so by sending a `GET`
request to his card ID. He should see something like:

```json
{
  "id": 1,
  "balance": 9799.05,
  "blocked": 199.95,
  "name": "Goron Elder"
}
```

Mr. Barten can later capture this money by sending a `PUT` request to the
transaction ID `/cards/1/transactions/1`:

```json
{
  "amount": 199.95,
  "type": "capture"
}

```
If Mr. Barten gets greedy and tries to capture too much, the law will step in:

```json
{
  "code": 400,
  "message": "Insufficient authorized amount remaining, £200.00 requested but £199.95 available"
}
```

Clock Town started using GBP only recently.

After a few more visits, the Goron Elder may wish to view his recent transactions.
He can check his statement by sending a `GET` request to `/statements` with his
card ID.

```json
[
  {
    "merchant": "Clock Town Milk Bar",
    "amount": 199.95,
    "timestamp": 1489354320000
  },
  {
    "merchant": "Clock Town Milk Bar",
    "amount": 199.95,
    "timestamp": 1489354916000
  }
  {
    "merchant": "Clock Town Milk Bar",
    "amount": 199.95,
    "timestamp": 1489355129000
  }
  {
    "merchant": "Clock Town Milk Bar",
    "amount": 199.95,
    "timestamp": 1489355784000
  }
]
```

He sure is a big fan!

## Future Improvements

* Moving to an on-disk database (probably PostgreSQL becaues of Heroku)
* Actually transferring the money to the merchants
