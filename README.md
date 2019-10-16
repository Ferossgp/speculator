# Speculator
# NOTE: This repository is not ready and will contain the code for the approach theoretically described in my Bachelor Thesis and [Paper](https://link.springer.com/chapter/10.1007%2F978-3-030-29852-4_5). It contains the code which was used to test the ability to use that approach (Translation of Clojure to Boogie).

Speculator is a tool to translate Clojure code to Boogie. The tool aims to find ahead of time errors in program. 

## Build

To compile clojure code use the aot alias:
```
$ mkdir classes
$ clojure -A:aot
```
Then you will need to build the jar using the following command:
```
$ clojure -A:pack mach.pack.alpha.capsule uberjar.jar -e classes -m speculator.main
```

## Usage

After you build uberjar you can use it in the following way:
```
$ uberjar.jar code.clj
```
This command generates a Boogie file with the same name plus bpl extenssion.

To run Boogie prover you will need to load MM.bpl from `resources/prelude/MM.bpl` and newly generated file into Boogie then run it.


## License

Copyright © 2019

Distributed under the Eclipse Public License, the same as Clojure.
