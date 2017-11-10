# User Resources
```
    GET users/{id}/courses
```

## Description
Returns an array of courses ID.

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

    https://localhost/users/59fafeb66af0fe7def998208/courses

**Return** __shortened for example purpose__
``` json
["titi","toto"]
```
