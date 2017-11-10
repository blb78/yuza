# User Resources
```
    DELETE users/{id}/courses/{id}
```

## Description
Delete a user course's

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
- 404 Not Found — User/Course with the specified ID does not exist.

***

## Example
**Request**
```
https://localhost/users/59fafeb66af0fe7def998208/courses/666
```
 

**Return** __shortened for example purpose__
``` json
    ["111","222","333","444","555","777","888"]
```
