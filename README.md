[![Build Status](https://travis-ci.org/kwyse/prepaid-card.svg?branch=master)](https://travis-ci.org/kwyse/prepaid-card)

# A Simple Prepaid Card API

Provides a REST API for common prepaid card operations, such as loading
money onto a card and merchants authorizing payment. Built with Dropwizard.
All data is stored in a PostgreSQL database.

You can play around with it [here](https://prepaid-card.herokuapp.com/cards).

## Getting Started

Build with Maven and then run pending database migrations. Ensure you have a
PostgreSQL server running locally and a database named `prepaidcard` owned by
a role named `pg` if you want the default settings to work. These details can
be changed in `config.yml`.

```bash
$ mvn package
$ java -jar ./target/prepaid-card-1.0-SNAPSHOT.jar db migrate config.yml
```

You can then start the server!

```bash
$ java -jar ./target/prepaid-card-1.0-SNAPSHOT.jar server config.yml
```

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

* Actually transferring the money to the merchants
