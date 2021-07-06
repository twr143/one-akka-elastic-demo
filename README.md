## CRUD of Employee demo with akka and elastic
### How to run
You need to run Elastic cluster and both **Service** and **Dao** servers.

Servers can be started by 'sbt service/runMain org.iv.EmpServer' and 'sbt dao/runMain org.iv.EmpDaoServer'<br>
For simplicity all crud requests are POST<br>
Create Employee:<br>
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/create -d '{"name" : "igor", "joined" : "2021-07-05T03:12:13"}'<br>
Query Employees:<br>
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/query -d '{"query" : "name:i*"}'<br>
Update by query:<br>
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/update -d '{"query" : "name:i*", "script":"ctx._source.name = ctx._source.name + '1' "}'<br>
Delete by query:<br>
curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/delete -d '{"query" : "name:ig*"}<br>