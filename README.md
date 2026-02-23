# Joymarket
Prototype of a mini market platform

Register isn't specified to be able to state roles, so the admin, and courier, must be initialized from the SQL DB (or through register).

Customer role logins will redirect to CustomerHomepage, with three navigation options; Top up, Add to cart, and edit profile views. The last button will be checkout, which only does the checkout logic and pays for the items added to the cart. The interface for viewing balance and what's inside the cart isnt implemented yet, though.

Admin role login will redirect to AdminHP, with two nav options; Edit product, and assign courier views. adding or renaming products were not mentioned, so the first view can only edit the stock of already initialized/available products in the DB.

Lastly, courier role logins will redirect to CourierHP, without any nav options, its just two combo boxes to view orders that courier has been assigned to, and another to change the status of that order.
