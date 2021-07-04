## CRUD of Employee demo with akka and elastic
### How to run
You need to run Elastic cluster and both **Service** and **Dao** servers.

Servers can be started by and 'sbt dao/runMain org.iv.EmpDaoServer'
For simplicity all crud requests are POST
Create Employee:
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/create -d '{"name" : "igor", "joined" : "2021-07-05T03:12:13"}'
Query Employees:
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/query -d '{"query" : "name:*"}'
Update by query:
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/update -d '{"query" : "name:i*", "script":"ctx._source.name = ctx._source.name + '1' "}'
Delete by query:
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/delete -d '{"query" : "name:ig*"}