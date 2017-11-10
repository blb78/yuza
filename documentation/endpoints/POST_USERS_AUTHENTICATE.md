# User Resources
```
    POST users/authenticate
```

## Description
Check credentials

***

## Requires authentication
None
***

## Parameters
### Body
Fields required 
- email
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
- 404 Not Found — User with the specified ID does not exist.

***

## Example
**Request**

    https://localhost/users/authenticate

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
