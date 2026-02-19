# Course App Service

A Spring Boot REST API for managing students, teachers, and courses. Supports CRUD operations and reporting queries such as counting members, filtering by group/course/age, and more.

## Future Ideas

- **Redis Caching** — introduce Redis for caching read-heavy report queries (counts, group/course lookups) when traffic increases. Some endpoints can tolerate eventual consistency, making them good candidates for cache-aside with a short TTL.
- **Idempotency Key** — add idempotency key support (via request header) for POST operations to prevent duplicate resource creation on retries.

## Tech Stack

- Java 21, Spring Boot 3.2
- Spring Data JPA, H2 (in-memory), Liquibase
- MapStruct, Lombok
- Log4j2, AOP logging
- Maven

## Getting Started

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

### Docker

```bash
docker compose up --build
```

### Running Tests

```bash
mvn clean test
```

## API Reference

Full request examples are available in the `api-docs/` folder (IntelliJ HTTP Client format).

Base URL: `http://localhost:8080/api`

---

### Courses

#### Create a course

`POST /api/courses`

Response `201 Created`:
```json
{
  "id": 1,
  "name": "Math",
  "type": "MAIN"
}
```

#### Get a course by ID

`GET /api/courses/1`

Response `200 OK`:
```json
{
  "id": 1,
  "name": "Math",
  "type": "MAIN"
}
```

#### Get all courses

`GET /api/courses`

Response `200 OK`:
```json
[
  { "id": 1, "name": "Math", "type": "MAIN" },
  { "id": 2, "name": "Art", "type": "SECONDARY" }
]
```

#### Update a course

`PUT /api/courses/1`

Response `200 OK`:
```json
{
  "id": 1,
  "name": "Advanced Math",
  "type": "MAIN"
}
```

#### Delete a course

`DELETE /api/courses/1`

Response `204 No Content`

---

### Members (Students & Teachers)

Each course can have at most one teacher. Assigning a second teacher returns `409 Conflict`.

#### Create a student

`POST /api/members`

Response `201 Created`:
```json
{
  "id": 1,
  "name": "Peter",
  "age": 20,
  "group": "A1",
  "type": "STUDENT",
  "courseIds": [1]
}
```

#### Create a teacher

`POST /api/members`

Response `201 Created`:
```json
{
  "id": 2,
  "name": "Prof Smith",
  "age": 45,
  "group": "A1",
  "type": "TEACHER",
  "courseIds": [1]
}
```

#### Get a member by ID

`GET /api/members/1`

Response `200 OK`:
```json
{
  "id": 1,
  "name": "Peter",
  "age": 20,
  "group": "A1",
  "type": "STUDENT",
  "courseIds": [1]
}
```

#### Get all students

`GET /api/members?type=STUDENT`

Response `200 OK`:
```json
[
  {
    "id": 1,
    "name": "Peter",
    "age": 20,
    "group": "A1",
    "type": "STUDENT",
    "courseIds": [1]
  }
]
```

#### Get all teachers

`GET /api/members?type=TEACHER`

Response `200 OK`:
```json
[
  {
    "id": 2,
    "name": "Prof Smith",
    "age": 45,
    "group": "A1",
    "type": "TEACHER",
    "courseIds": [1]
  }
]
```

#### Update a member

`PUT /api/members/1`

Response `200 OK`:
```json
{
  "id": 1,
  "name": "Peter Updated",
  "age": 21,
  "group": "B1",
  "type": "STUDENT",
  "courseIds": [1, 2]
}
```

#### Delete a member

`DELETE /api/members/1`

Response `204 No Content`

---

### Reports

#### How many students we have

`GET /api/reports/members/count?type=STUDENT`

Response `200 OK`:
```json
{
  "count": 4
}
```

#### How many teachers we have

`GET /api/reports/members/count?type=TEACHER`

Response `200 OK`:
```json
{
  "count": 2
}
```

#### How many courses by type we have

`GET /api/reports/courses/count?type=MAIN`

Response `200 OK`:
```json
{
  "count": 2
}
```

`GET /api/reports/courses/count?type=SECONDARY`

Response `200 OK`:
```json
{
  "count": 1
}
```

#### Which students participate in a specific course

`GET /api/reports/courses/members?courseId=1&type=STUDENT`

Response `200 OK`:
```json
[
  {
    "id": 1,
    "name": "Peter",
    "age": 20,
    "group": "A1",
    "type": "STUDENT",
    "courseIds": [1]
  },
  {
    "id": 3,
    "name": "Jane",
    "age": 22,
    "group": "A1",
    "type": "STUDENT",
    "courseIds": [1]
  }
]
```

#### Which members belong to a specific group

`GET /api/reports/groups/members?group=A1`

Response `200 OK`:
```json
[
  {
    "id": 1,
    "name": "Peter",
    "age": 20,
    "group": "A1",
    "type": "STUDENT",
    "courseIds": [1]
  },
  {
    "id": 2,
    "name": "Prof Smith",
    "age": 45,
    "group": "A1",
    "type": "TEACHER",
    "courseIds": [1]
  }
]
```

#### Find all teachers and students for a specific group and course

`GET /api/reports/groups/courses?group=A1&courseId=1`

Response `200 OK`:
```json
{
  "group": "A1",
  "courseId": 1,
  "members": [
    {
      "id": 1,
      "name": "Peter",
      "age": 20,
      "group": "A1",
      "type": "STUDENT",
      "courseIds": [1]
    },
    {
      "id": 2,
      "name": "Prof Smith",
      "age": 45,
      "group": "A1",
      "type": "TEACHER",
      "courseIds": [1]
    }
  ]
}
```

Response `404 Not Found` (when no members match):
```json
{
  "error": "No members found for group: B2 and course id: 1"
}
```

#### Find all students older than a specific age in a specific course

`minAge` is inclusive (age >= value).

`GET /api/reports/members/filter?minAge=21&courseId=1&type=STUDENT`

Response `200 OK`:
```json
[
  {
    "id": 3,
    "name": "Jane",
    "age": 22,
    "group": "A1",
    "type": "STUDENT",
    "courseIds": [1]
  },
  {
    "id": 4,
    "name": "Alice",
    "age": 25,
    "group": "A1",
    "type": "STUDENT",
    "courseIds": [1, 3]
  }
]
```

---

### Error Responses

#### Resource not found

```json
{
  "error": "Course not found with id: 999"
}
```
Status: `404 Not Found`

#### Validation error

```json
{
  "name": "must not be blank",
  "type": "must not be null"
}
```
Status: `400 Bad Request`

#### Duplicate teacher

```json
{
  "error": "A teacher is already assigned to course with id: 1"
}
```
Status: `409 Conflict`
