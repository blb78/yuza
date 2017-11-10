# User Resources
```
    PUT users/{id}/courses/{id}
```

## Description
Add a course ID in an array of course ID.

***

## Requires authentication
* A valid JWT must be provided.

***

## Parameters
None


***

## Return format
An array of courses ID

***

## Errors
- 404 Not Found — User with the specified ID does not exist.

***

## Example
**Request**

    https://localhost/users/5a05ca6d521a52467080aad5/courses/1234

**Return** __shortened for example purpose__
``` json
["666","1234"]
```
