# User Resources
```
    GET users
```

## Description
Returns a pageable list of users.

***

## Requires authentication
* A valid JWT must be provided.

***

## Parameters

- **page** — Return a specific page in the user collection. Page numbering is 1-based.
- **size** — The number of results to return, default 20.


***

## Return format
An array with the following keys and values:

- **content** — List of users found.
- **last** — bool for the last page
- **totalElements** — Number of elements in collection.
- **totalPages** — Total number of pages in order to show the full collection.
- **first** — bool for the first page
- **numberOfElements** — Number of elements returns
- **size** - The number of results to return
- **number** - I don't know

***

## Errors
None

***

## Example
**Request**

    https://localhost/users

**Return** __shortened for example purpose__
``` json
{
    "content":[
            {
                "id":"59fafeb66af0fe7def998208",
                "email":"tutu@tutu.com",
                "firstName":"tutu",
                "lastName":"tutu",
                "courses":[
                    "titi",
                    "toto"
                    ],
                "birthday":0
            },
            {
                "id":"5a0593f1521a523bed7809fd",
                "email":"aze@aze.com",
                "firstName":"aze",
                "lastName":"aze",
                "courses":[],
                "birthday":0
            }
        ],
    "last":true,
    "totalElements":2,
    "totalPages":1,
    "first":true,
    "numberOfElements":2,
    "size":20,
    "number":0
}
```
