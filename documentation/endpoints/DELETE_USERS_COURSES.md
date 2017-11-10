# User Resources
```
    DELETE users/{id}/courses
```

## Description
Delete all user courses ID's 

***

## Requires authentication
* A valid JWT must be provided.

***

## Parameters

None

***

## Return format
None, just a status code 200

***

## Errors
- 404 Not Found — User with the specified ID does not exist.

***

## Example
**Request**
```
https://localhost/users/59fafeb66af0fe7def998208/courses
```
 

**Return** __shortened for example purpose__
``` json
    []
```
