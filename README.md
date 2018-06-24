# Interview Scheduler

The interview scheduler (iwscheduler) is a small application, built primarily for demo and teaching purposes. It was written in Kotlin and uses Spring Boot for dependency injection and component orchestration. The goal of this application is to demonstrate a simple end-to-end Spring Boot application, by wrapping it in a simple case scenario. It has one main API endpoint, which allows for checking the matching availability between interview candidates, and interviewers (assuming that the availability of the separate parties has been saved in the system upfront).

## Setup
The project uses Gradle for building. Gradle has its one wrapper for instances that don’t necessarily have all the necessary components. It’s always best to start setting up executing the Gradle wrapper. 

```bash
./gradlew.sh wrapper
```

Having let the wrapper download dependencies, let’s go ahead and start the application:

```bash
./gradlew.sh bootRun
```

Spring Boot applications are ordinary JVM applications, having a `main()` method as part of a regular class. In our case, this class  is called `IviewSchedulerApplication`. Besides the main method bootstrapping it as a Spring Boot application, this class has one more method, which is automatically being called post-construction, and helps set up a demo environment. By default, it will set up the availability schedule for one interview candidate `cand1` and two interviewers (`iview1` and `iview2`)

Once the application is running, one can access the main API endpoint for searching time slot availability. It has two URL params: one for specifying the candidate's database ID, and the other for a comma-separated list of interviewer IDs. Using the demo parameters mentioned above, one call involving all parties will look like this:

```
GET /appointment/search?candidateId=cand1&interviewerIds=iview1,iview2
```

Depending on the data in the database, one might add more interviewers, or leave it to just one, as here:
```
GET /appointment/search?candidateId=cand1&interviewerIds=iview1
```

## How to Explore the Code Structure
When exploring the code, the `IviewSchedulerApplication` is a good point to start. There are two other points which can be a good start:
1. `AvailabilityServiceUTest` - a unit test that uses the same demo  data set up as part of `IviewSchedulerApplication` and tests the validity of the availability search API.
2. `AvailabilityController` - being the front-facing layer of the API, it presents a great starting point for digging deeper.

## Overall Architecture
Architecturally, iwscheduler follows a standard 3-layer architecture. It stars with a thin MVC layer communicating the API to clients. This front-facing layer furthers requests to a business logic segment. It is responsible for doing the heavy-duty business thinking, and if necessary, sending requests one level deeper to the persistence layer for fetching or persisting data from/to the database.

Having it all layered this way, helps reduce coupling, since every layer only needs to know the next one.

### Persisting the time slots
Instead of storing each appointment as a separate object for, multiple appointments per person are persisted as bits in a given day. What does this mean?

If we imagine a day of possible 1-hour time slots, booking a single time slot between 4 PM and 5 PM will end up looking like this:

```
00000000000000100000000
```

Having three 1-hour time slots within the same day, might look something like this:
```
00000100001000100000000
```

One benefit of using bits for representing time slots is that one can literally store up to 24 day time slots inside a single integer.

The real benefit of using this approach over storing availabilities as separate time range entities however, is the ease and speed of finding matching time slots. To get only the matching time slots of a given day, all one has to do, is fetch the given availability day information for all parties and simply apply a binary AND operation over them:

```
matchingTimeSlots = candTimeSlots AND iview1TimeSlots AND iview2TimeSlots AND ...
```

Once the matching bits have been found, one can then convert them to `TimeSlot` objects for easy API client consumption.
