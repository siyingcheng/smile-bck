# Authentication and Authorization

This is the story of how authentication and authorization work in a web e-commerce application.

## User Roles

There are three main user roles in a web e-commerce application:

1. Guest: A user who is not logged in and can only browse the website.
2. Customer and Seller: A user who is logged in and can browse the website, add products to their cart, and place orders. Also, a seller can manage their products and orders.
3. Admin: A user who is logged in and can manage the website, including users, products, and orders.

## Authentication

Authentication is the process of verifying a user's identity. In a web e-commerce application, authentication is done using a username and password.

When a user logs in, their credentials are checked against the database to verify their identity. If the credentials are correct, the user is granted access to the website.

## Authorization

Authorization is the process of granting a user access to a particular resource or functionality. In a web e-commerce application, authorization is based on the user's role and their permissions.

## Registering a User

When a user registers on the website, their details are stored in the database. Their role is set to "customer" by default. Their can use their email address or username to log in to the website.

Password requirements:
- Passwords must be at least 8 characters long.
- Passwords must contain at least one uppercase letter, one lowercase letter, and one number.
- Passwords must contain at least one special character.
- Passwords will not be stored in plain text.
- The user could reset their password by email if they forget it.

## Rules of Authorization

There are several rules of authorization that must be followed in a web e-commerce application:

1. Guests can only browse the website.
2. Customers and sellers can browse the website, add products to their cart, and place orders.
3. Sellers can manage their products and orders.
4. Admins can manage the website, including users, products, and orders.
5. Only admins can create, edit, and delete users.
6. Only admins can create, edit, and delete products.
7. Only admins can create, edit, and delete orders.

