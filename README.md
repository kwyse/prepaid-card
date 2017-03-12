# A Simple Prepaid Card API

Provides a REST API for common prepaid card operations, such as loading
money onto a card and merchants authorizing payment. All data is stored in
an in-memory SQLite database.

# Future Improvements

* Adding a statements endpoint
* Moving to an on-disk database
* Refactoring out the Card class into an interface and having non-blocked
  and blocked subclasses
* Actually transferring the money to the merchants
