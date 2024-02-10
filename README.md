# pcb.cljc

aims at being compatible w/ Kicad's S-Expression formats ([doc](https://dev-docs.kicad.org/en/file-formats/)), itself based upon [Specctra DSN](https://en.wikipedia.org/wiki/Specctra)'s.

could use instparse ([example w/ S-Expressions](http://andrevdm.blogspot.com/2014/02/parsing-s-expressions-in-clojure.html)) or just directly use `spec` ([example](https://www.juxt.pro/blog/parsing-with-clojure-spec/)) or even macros.

lein template from https://github.com/shadow-cljs/lein-template

## Run

``` shell
yarn install

yarn watch
```

## Clean

``` shell
yarn clean
```

## Release

``` shell
yarn release
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
