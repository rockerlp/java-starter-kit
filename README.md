# MoreApp Java Starter Kit

An example project to help MoreApp partners with their first usage of the API.

## Run

```
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## Example code

Check out the file `src/main/java/com/moreapp/starterkit/Example.java` to see interaction with the API.

The example code reads a properties file from disc.

```
Properties properties = new Properties();
properties.load(Example.class.getResourceAsStream(...));
```

It sets up an client with authorization settings (using the loaded properties file).

```
OAuthRestTemplate oAuthRestTemplate = prepareClient(properties);
```

The client can be used to make an authorized call to the MoreApp API. The example code will fetch all customers for the authorized partner.

```
ResponseEntity<Map[]> response = oAuthRestTemplate.getForEntity(endpoint + "/customers", Map[].class);
System.out.println("The response status is: " + response.getStatusCode());
if (response.getStatusCode().value() != 200) {
    System.out.printf("Something went wrong please check the credentials and the API endpoint");
    return;
}
```

Finaly the code will output the result.

```
System.out.println("The customers are:");
Map[] customers = response.getBody();
for (Map customer : customers) {
    System.out.println(" - " + customer.get("name"));
}
```

## Changing the example for your own usage

To use the example for your own usage change the file `src/main/resources/com/moreapp/starterkit/api.properties`.

- The `endpoint` should be `https://api.moreapp.com/api/v1.0`. For the example we the MoreApp develop environment. Please do not use this.  
- The `salt` property should be changed into the salt that you can acquire in de developer portal under FAQ. 
- The `consuemerKey` and `consumerSecret` properties should be changed into the correct partner credentials.

