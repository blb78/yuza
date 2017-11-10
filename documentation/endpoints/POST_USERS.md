# User Resources
```
    POST users
```

## Description
Returns a user object.

***

## Requires authentication
* A valid JWT must be provided in **Authorization**  header parameter's.

***

## Parameters
### Body
Fields required 
- email
- firstName
- lastName
- password


***

## Return format
A object with the following keys and values:

- **id** — List of users found.
- **email** — bool for the last page
- **firstName** — Number of elements in collection.
- **lastName** — Total number of pages in order to show the full collection.
- **birthday** — bool for the first page

***

## Errors
- 409 Conflict — User with the specified email already exist.

***

## Example
**Request**

    https://localhost/users

**Return** __shortened for example purpose__
``` json
{
    "id":"5a05ca6d521a52467080aad5",
    "email":"qwerty@qwerty.com",
    "firstName":"qwerty",
    "lastName":"qwerty",
    "courses":[],
    "birthday":0
}
```
