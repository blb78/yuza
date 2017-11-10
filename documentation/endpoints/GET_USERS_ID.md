# User Resources
```
    GET users/{id}
```

## Description
Returns a user object

***

## Requires authentication
* A valid JWT must be provided in **Authorization**  header parameter's.

***

## Parameters

None

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
```
https://localhost/users/59fafeb66af0fe7def998208
```
 

**Return** __shortened for example purpose__
``` json
{
"id": "59fafeb66af0fe7def998208",
"email": "tutu@tutu.com",
"firstName": "tutu",
"lastName": "tutu",
"courses": [
  "titi",
  "toto"
],
"birthday": 0
}
```
