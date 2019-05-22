This repo contains the Annotated and unannotated version of:-
https://www.nayuki.io/page/native-hash-functions-for-java

Annotated version is on ranch patch-1
Unannotated version is on master

To compile from terminal see MakeFile.

In this case study I learnt that signedness checker is weak in some classes and needs enhancements such as:-

1.)java.util.Random- nextBytes function

2.)Some issues came repeatedly like Issue #2482, #2367.

and I think many more in the future too.

When you will run the test file using signedness checker these errors will come-

```
nayuki/nativehash/HashTest.java:53: error: [argument.type.incompatible] incompatible types in argument.
				random.nextBytes(b);
				                 ^
  found   : @Unsigned byte @UnknownSignedness []
  required: @Signed byte @UnknownSignedness []
nayuki/nativehash/HashTest.java:76: error: [argument.type.incompatible] incompatible types in argument.
			random.nextBytes(b);
			                 ^
  found   : @Unsigned byte @UnknownSignedness []
  required: @Signed byte @UnknownSignedness []
nayuki/nativehash/Tiger2Java.java:16: warning: [cast.unsafe] cast from "@IntVal(128) int" to "@IntVal(-128) byte" cannot be statically verified
		padding = (byte)0x80;
		          ^
2 errors
1 warning
```

When you will run the demo file using signedness checker this error will come-
```
nayuki/nativehash/BenchmarkHashes.java:64: error: [argument.type.incompatible] incompatible types in argument.
		new Random().nextBytes(buffer);
		                       ^
  found   : @Unsigned byte @UnknownSignedness []
  required: @Signed byte @UnknownSignedness []
1 error
```
