# api-evaluator
End to end test and score provided api

# Sample command line
```shell
BUILD---
clean fatJar

Run
java -jar api-evaluator.jar -csv Identity_personification.csv -pTEST_DATA_DIRECTORY=/D://Hackathon_2024/workspace/challenge-artifacts/identity-personification/dataset -tc identity-personification.yaml -o Identity_personification_step1_score.csv -l /D://Hackathon_2024/workspace/api-evaluator/testcases/logback.xml --info
```
