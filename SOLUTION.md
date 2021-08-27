# Solution

## Part 1

Utilizing the debugger I set a breakpoint at the failing test and followed changes/loadings made to variables _(besides trying to get head around Spring after sometime... 😅 )_.

Both Pans `4319000000000001` and `5432000000000001` are loaded from the `"bin-range-info-data.json"` file on lines 61-63 in the CachingBinRangeService class, thus these tests should not fail.

Additionally Pans `4263000000000001` and `5263000000000001` are loaded via the getLatestBinRangeInfo function within the test file, thus cachingBinRangeService should not return null, these tests are working as expected.

I added an addition Pan to test (confirm) that cachingBinRangeService will return `null` when a Pan cannot be found _(or when its removed from the cache.. I could have implemented functionality but that seemed overkill so this test will suffice)_. 🙌

<hr>

## Part 2

`BinRangeService` interface updated to define service logic functions - coupled to BinRangeInfo.
I modified the `BinRangeInfoCacheController` to be loosely coupled by using the `BinService` interface instead of its actual implementation.
Every test was created using Test Driven Development principles.

Examples are using [HTTPie](https://httpie.io/) instead of `curl` (I messed around with my curl settings and never changed them back 😑).

### Endpoints

#### GET

Not required but I implemented it for my own testing.

##### Example Usage

```bash
http GET http://localhost:8080/binRangeInfoCache/
```

```json
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Sun, 25 Jul 2021 16:50:12 GMT
Transfer-Encoding: chunked

{
    "2a480c8a-83ca-4bb7-95b7-f19cec97b3fd": {
        "bankName": "AIB",
        "currencyCode": "EUR",
        "end": 4263999999999999,
        "ref": "2a480c8a-83ca-4bb7-95b7-f19cec97b3fd",
        "start": 4263000000000000
    },
    "7decd7db-1e93-4a44-beb9-d71e277a8239": {
        "bankName": "TSB",
        "currencyCode": "GBP",
        "end": 5432999999999999,
        "ref": "7decd7db-1e93-4a44-beb9-d71e277a8239",
        "start": 5432000000000000
    },
    "d893d80c-e7cf-4ba7-9f26-ae289d29e136": {
        "bankName": "BOI",
        "currencyCode": "USD",
        "end": 4319999999999999,
        "ref": "d893d80c-e7cf-4ba7-9f26-ae289d29e136",
        "start": 4319000000000000
    }
}
```

<hr>

#### POST

##### Edge Cases

- If the Bin Range already exists.
- Data Validation.
- Ref can be supplied but it will be replaced with the server side generated ref.
- If the BinRange already exists.

##### Assumptions

- Data Validation: `start`, `end`, `bankName` and `currencyCode` cannot be `null` values. `Ref` can be `null` since its generated on the server side and if it is provided its overridden.

##### Responses

###### 201

```json
http POST http://localhost:8080/binRangeInfoCache start=1111000000000000 end=1111999999999999 bankName=test currencyCode=EUR

HTTP/1.1 201
Content-Type: application/json;charset=UTF-8
Date: Sun, 25 Jul 2021 16:35:17 GMT
Transfer-Encoding: chunked

{
    "bankName": "test",
    "currencyCode": "EUR",
    "end": 1111999999999999,
    "ref": "846c552e-fad0-411a-b3c4-a91b692cf0d4",
    "start": 1111000000000000
}
```

###### 409

```json
http POST http://localhost:8080/binRangeInfoCache start=1111000000000000 end=1111999999999999 bankName=test currencyCode=EUR

HTTP/1.1 409
Content-Length: 66
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:05:06 GMT

BinRangeInfo: 846c552e-fad0-411a-b3c4-a91b692cf0d4 already exists.
```

<hr>

#### PUT

##### Edge Cases

- Start and End range cannot be modified.
- The path variable i.e. ref and Bin Range ref should match _(not a business requirement but its best for validation)_.
- If the BinRange requested to update doesnt exist.

##### Assumptions

- Since a ref needs to be provided via a PathVariable and the json payload i.e. `BinRangeInfo` also provides a ref value they need to match.
  You could implement logic to set the json payload ref if its not provided but I think its best the user also provides it to ensure the correct BinRange is being updated. If the refs dont match a `BinRangeInfoInvalidException` is thrown.

- The start and end range cannot be modified once inserted. If they dont a `BinRangeInfoInvalidException` is thrown.

- Data Validation.

##### Responses

###### 200

```json
http PUT http://localhost:8080/binRangeInfoCache/2a480c8a-83ca-4bb7-95b7-f19cec97b3fd ref=2a480c8a-83ca-4bb7-95b7-f19cec97b3fd start=4263000000000000 end=4263999999999999 currencyCode=EUR bankName=AIB_TEST

HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:14:51 GMT
Transfer-Encoding: chunked

{
    "bankName": "AIB_TEST",
    "currencyCode": "EUR",
    "end": 4263999999999999,
    "ref": "2a480c8a-83ca-4bb7-95b7-f19cec97b3fd",
    "start": 4263000000000000
}
```

###### 409

```json
http PUT http://localhost:8080/binRangeInfoCache/2a480c8a-83ca-4bb7-95b7-f19cec97b3fd ref=846c552e-fad0-411a-b3c4-a91b692cf0d4 start=1263000000000000 end=1263999999999999 currencyCode=EUR bankName=AIB

HTTP/1.1 409
Content-Length: 99
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:09:57 GMT

Ref values 2a480c8a-83ca-4bb7-95b7-f19cec97b3fd : 846c552e-fad0-411a-b3c4-a91b692cf0d4 don't match.
```

###### 404

```json
http PUT http://localhost:8080/binRangeInfoCache/9651794E-8166-423F-8B8D-EE235A04DDB7 ref=9651794E-8166-423F-8B8D-EE235A04DDB7 start=5263000000000000 end=5263999999999999 currencyCode=EUR bankName=invalid

HTTP/1.1 404
Content-Length: 65
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:18:13 GMT

BinRangeInfo: 9651794e-8166-423f-8b8d-ee235a04ddb7 was not found.
```

###### 409

```json
http PUT http://localhost:8080/binRangeInfoCache/2a480c8a-83ca-4bb7-95b7-f19cec97b3fd ref=2a480c8a-83ca-4bb7-95b7-f19cec97b3fd start=4263000000000000 end=4263999999999990 currencyCode=EUR bankName=AIB

HTTP/1.1 409
Content-Length: 30
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:15:55 GMT

Bin Ranges cannot be modified.
```

<hr>

#### DELETE

##### Edge Cases

- If the BinRange requested to be deleted doesnt exist.

##### Responses

###### 200

```json
http DELETE http://localhost:8080/binRangeInfoCache/846c552e-fad0-411a-b3c4-a91b692cf0d4

HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:20:27 GMT
Transfer-Encoding: chunked

{
    "bankName": "test",
    "currencyCode": "EUR",
    "end": 1111999999999999,
    "ref": "846c552e-fad0-411a-b3c4-a91b692cf0d4",
    "start": 1111000000000000
}

```

###### 404

```json
http DELETE http://localhost:8080/binRangeInfoCache/9651794E-8166-423F-8B8D-EE235A04DDB7

HTTP/1.1 404
Content-Length: 65
Content-Type: text/plain;charset=UTF-8
Date: Mon, 26 Jul 2021 14:20:51 GMT

BinRangeInfo: 9651794e-8166-423f-8b8d-ee235a04ddb7 was not found.
```

#### Data Validation

The `POST` and `PUT` endpoints have simple data validation to ensure the correct data is being provided.

##### Example

```json
http POST http://localhost:8080/binRangeInfoCache start=1111000000000000 end=1111999999999999 bankName=test

HTTP/1.1 400
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Mon, 26 Jul 2021 14:22:40 GMT
Transfer-Encoding: chunked

{
    "error": "Bad Request",
    "errors": [
        {
            "arguments": [
                {
                    "arguments": null,
                    "code": "currencyCode",
                    "codes": [
                        "binRangeInfo.currencyCode",
                        "currencyCode"
                    ],
                    "defaultMessage": "currencyCode"
                }
            ],
            "bindingFailure": false,
            "code": "NotNull",
            "codes": [
                "NotNull.binRangeInfo.currencyCode",
                "NotNull.currencyCode",
                "NotNull.java.lang.String",
                "NotNull"
            ],
            "defaultMessage": "must not be null",
            "field": "currencyCode",
            "objectName": "binRangeInfo",
            "rejectedValue": null
        }
    ],
    "message": "Validation failed for object='binRangeInfo'. Error count: 1",
    "path": "/binRangeInfoCache",
    "status": 400,
    "timestamp": "2021-07-26T14:22:40.747+0000"
}
```

<hr>

## Part 3

I updated the `Event` interface to be generic so it can handle multiple different types of audit events (this violates the YAGNI but I'm positive if this was a solution in production a new type of audit event would eventually be required. `AuditSubscriber` was also updated to handle the modified Event interface.

I didnt implement tests for EventBus because they werent asked for but if I were too I'd mock EventBus and ensure register was called `x times` and for the `publishAsync` method called `EventBus.post`.

I added additional tests for `AuditEvent`, specifically its `toString` method to ensure its outputting the valid format for logs.

### Example Log File

```log
createdAt={Wed Jul 28 17:07:01 BST 2021}, before={BinRangeInfo: ref=31fde6df-b594-477c-af57-f436b0c04842, start=2398000000000000, end=2398999999999999, bankName=Rempel, Rempel and Rempel, currencyCode=BRL}, after={BinRangeInfo: ref=31fde6df-b594-477c-af57-f436b0c04842, start=8764000000000000, end=8764999999999999, bankName=Cartwright LLC, currencyCode=NGN}
```

## Conclusion

Hopefully this solution suffices, it took me a while (a lot of Googling) to figure out how Spring works again along with Java's testing libraries but I got there in the end (`;` _are a nuisance after using Python for so long_) :).
