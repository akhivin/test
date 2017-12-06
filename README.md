Usage
-----

exit - stop execution
 
goto <n> - user pressed button inside elevator. n - target floor number

floor <n> - user pressed hall button on a floor n

Implementation
--------------
There are three threads in application (actually Runnable, running used FixedThread pool)

* LiftControlAdapter - reads user input and translates it to the comman objects; 
* LiftEngine - runs actual simulation. Executes user commands due to its priority using 
PriorityBlockingQueue;
* LiftMonitorAdapter - prints Events obtained from LiftEngine.

All of the objects interchanged between threads are immutable

Build
-----
To build use 
```bash
mvn package
```

Running
-----
To run 

```
java -jar <floors> <floor height> <lift speed> <waiting period>
```

For example
```
java -jar ./target/khivin-0.1.jar 10 10 5 1
```


