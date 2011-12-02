# Spock Extensions

## Maven:

    <dependency>
        <groupId>com.goldin</groupId>
        <artifactId>spock-extensions</artifactId>
        <version>0.1</version>
        <scope>test</scope>
    </dependency>


## Gradle:

    testCompile 'com.goldin:spock-extensions:0.1'

## Repo:

    http://evgeny-goldin.org/artifactory/repo

## Issue Tracker:

    http://evgeny-goldin.org/youtrack/issues/sp


## @With

`@With` extension can be applied per-Specification (globally) or per-feature (locally). It behaves as if it wraps the entire spec
or certain feature with a `with{ .. }` block using objects specified as delegates. As is the case with [`with`](http://groovy.codehaus.org/groovy-jdk/java/lang/Object.html#with%28groovy.lang.Closure%29)
it saves on referencing the same object over and over or creating temporal variables *"just to hold this result for a second"*.

It accepts a `Closure` expression returning either a single object or `List` of objects:

    @With({ true })
    @With({ 'string' })
    @With({ 'http://gradle.org/'.toURL() })

    @With({ [ 'string', [ 1 : 3 ], [ true ] ] })
    @With({ [ 'http://gradle.org/'.toURL(), 'http://groovy.codehaus.org/' ] })

All non-null objects returned by the `Closure` become `with{ .. }`-like delegates for execution of all `Specification` features
(if `@With` is applied globally) or execution of a certain feature (if applied locally). If extension is applied both globally
and locally then two sets of objects are combined, giving priority to those specified locally for the test method.

Internally, `@With` is not implemented using `with{ .. }` but MOP's [`methodMissing`](http://groovy.codehaus.org/Using+methodMissing+and+propertyMissing) and [`propertyMissing`](http://groovy.codehaus.org/Using+methodMissing+and+propertyMissing)
which means it works equally well for methods and properties.

### [Test Specifications](https://github.com/evgeny-goldin/spock-extensions/tree/master/src/test/groovy/com/goldin/spock/extensions)
### Examples (taken from [those](https://github.com/evgeny-goldin/gcommons/blob/90e6e100339c642a7d7b1d7ff33dd29cc58d653c/src/test/groovy/com/goldin/gcommons/specs/FileBeanSpec.groovy) [two](https://github.com/evgeny-goldin/gcommons/blob/90e6e100339c642a7d7b1d7ff33dd29cc58d653c/src/test/groovy/com/goldin/gcommons/specs/GeneralBeanSpec.groovy) files)

    @With({ [ 'string', [ 1 : 2 ], [ true ] ] })
    def 'regular test method' () {

        expect:
        bytes             // 'string'.bytes
        chars             // 'string'.chars
        size() == 6       // 'string'.size()
        containsKey( 1 )  // [ 1 : 2 ].containsKey( 1 )
        first()           // [ true ].first()
    }


    @With({ 'http://gradle.org/'.toURL() })
    def 'URL test method' () {

        when:
        // 'http://gradle.org/'.toURL().text
        new File( testDir, 'data.txt' ).write( text )

        then:
        new File( testDir, 'data.txt' ).text.size() > ( 9 * 1024 )
    }


    @With({ GCommons.file() })
    class FileBeanSpec extends Specification
    {
        def 'check relative path'( String dir, String file, String path )
        {
            expect:
            // GCommons.file().relativePath()
            relativePath( new File( dir ), new File( file )) == path

            where:
            dir             | file                   | path
            'C:/111/'       | 'C:/111/222/sss/3.txt' | '/222/sss/3.txt'
            'C:/'           | 'C:/111/222/oiu/3.txt' | ( windows ? '' : '/' ) + '111/222/oiu/3.txt'
            'C:/'           | 'C:/111/222/oiu/3.txt' | ( windows ? '' : '/' ) + '111/222/oiu/3.txt'
            'C:/1/2/'       | 'C:/1/2/'              | '/'
        }
    }


    class GeneralBeanSpec extends Specification
    {
        @With({ GCommons.general() })
        def 'check match()'()
        {
            expect:
            // GCommons.general().match()
            match( '/a/b/c/d', '**' )
            match( '/a/b/c/d/1.txt', '**/*.*' )
          ! match( '/a/b/c/d', '**/*.*' )
          ! match( '/a/b/c/d/1.txt', '**/*.xml'  )
        }
    }


## @Time


`@Time` extension can be applied per-Specification (globally) or per-feature (locally). It allows to time-limit execution of the whole spec or specific test method with two attributes:

* `min` (int) - `0` by default, minimal execution time in milliseconds, should be zero or more.
* `max` (int) - `Integer.MAX_VALUE` by default, maximal execution time in milliseconds, should be more than `min`.

Note, that both attributes are `int` (covering 24-days execution) and not `long` although they deal with milliseconds. This is to avoid warnings around `0L` constants and avoid appending `L` to numbers as well.
Default values allow to display an execution time without applying any limits:

![@Time log](https://dl.dropbox.com/s/so4eetmff17l8wf/Time%20Extension.png?dl=1)


### [Test Specifications](https://github.com/evgeny-goldin/spock-extensions/tree/master/src/test/groovy/com/goldin/spock/extensions)
### Example (taken from [this file](https://github.com/evgeny-goldin/gcommons/blob/a4abda41f5977c742b202d6d22a326699e6da7bf/src/test/groovy/com/goldin/gcommons/specs/GeneralBeanSpec.groovy))


    @Time( min = 500, max = 2000 )
    class GeneralBeanSpec extends Specification
    {
        @Time( min = 500, max = 2000 )
        def 'gc-87: GeneralBean.executeWithResult()'()
        {
            ...
        }


        @Time( min = 0, max = 100 )
        def 'check match()'()
        {
            ...
        }
    }


## @TestDir


`@TestDir` extension can only be applied to `Specification` instance field. It creates empty test directory for each Spock feature (test method) and assigns
its value to the field, assuming it is of `File` type.

It has two attributes

* `baseDir` (String) - `"build/test"` by default.
   Directory where all test directories are created.
* `clean`  (boolean) - `true` by default.
   If test directory already exists whether it should be cleaned up (`true` value) or another one should be created next to it (`false` value).

For each feature test directory created at `"<baseDir>/<spec FQCN>/<feature name>"` where all non-alphabetic characters in feature name are replaced by `"-"`.
For example for feature `'Check pack() and unpack() operations'` in `FileBeanSpec` it will be `build/test/com.goldin.gcommons.specs.FileBeanSpec/Check-pack-and-unpack-operations/`.

### [Test Specifications](https://github.com/evgeny-goldin/spock-extensions/tree/master/src/test/groovy/com/goldin/spock/extensions)
### Example (taken from [this file](https://github.com/evgeny-goldin/gcommons/blob/87484d54f0065f7e73008d4eabf1ea507b0922e4/src/test/groovy/com/goldin/gcommons/specs/FileBeanSpec.groovy))


    class FileBeanSpec extends Specification
    {
        @TestDir File testDir

        def 'Check pack() and unpack() operations' ()
        {
            // build/test/com.goldin.gcommons.specs.FileBeanSpec/Check-pack-and-unpack-operations/
            assert testDir.directory && ( ! testDir.listFiles())

            given:
            def zipUnpack1 = new File( testDir, 'zip-1' )
            def zipUnpack2 = new File( testDir, 'zip-2' )
        }


        def 'Check "fullpath" and "prefix" pack() options'()
        {
            // build/test/com.goldin.gcommons.specs.FileBeanSpec/Check-fullpath-and-prefix-pack-options/
            assert testDir.directory && ( ! testDir.listFiles())

            given:
            def zipFile    = testResource(      "${testArchive.key}.zip" )
            def zipUnpack  = new File( testDir, 'zip' )
            def extFile1   = new File( testDir, "${testArchive.key}-1.$extension" )
            def extFile2   = new File( testDir, "${testArchive.key}-2.$extension" )
        }
    }


## @TempDir

**Originally forked from `http://github.com/robfletcher/spock-extensions`.**

Used on a `File` property of a spec class this annotation will cause
a temporary directory to be created and injected before each feature
method is run and destroyed afterwards. If the field is `@Shared` the
directory is only destroyed after all feature methods have run. You
can have as many such fields as you like in a single spec, each will
be generated with a unique name.

Temporary directories are created inside `java.io.tmpdir`.

This is useful when testing a class that reads from or writes to a
location on disk.

### [Test Specifications](https://github.com/evgeny-goldin/spock-extensions/tree/master/src/test/groovy/com/goldin/spock/extensions)
### Example

    class MySpec extends Specification {

        @TempDir File myTempDir

        def diskStore = new DiskStore()

        def "disk store writes bytes to a file"() {
            given:
            diskStore.baseDir = myTempDir
            diskStore.targetFilename = "foo"

            when:
            diskStore << "some text"

            then:
            new File(myTempDir, "foo").text == "some text"
        }

    }
