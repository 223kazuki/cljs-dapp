# duct-ethereum-dapp

## Development

This is a Ethereum dApp using [re-frame](https://github.com/Day8/re-frame).

### Development Mode

#### Deploy contract locally:

1. You need to install following commands.

* truffle
* ganache-cli
* node/npm

2. Open terminal and run ganache-cli.

```sh
ganache-cli -i 1533140371286
```

3. Open other terminal window and run following commands.

```sh
npm install
truffle migrate --reset
```

#### Start Cider from Emacs:

Put this in your Emacs config file:

```
(setq cider-cljs-lein-repl
    "(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL with `cider-jack-in-clojurescript` or (`C-c M-J`)

#### Run application:

```
lein dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

#### Run tests:

```
lein clean
lein doo phantom test once
```

The above command assumes that you have [phantomjs](https://www.npmjs.com/package/phantomjs) installed. However, please note that [doo](https://github.com/bensu/doo) can be configured to run cljs.test in many other JS environments (chrome, ie, safari, opera, slimer, node, rhino, or nashorn).

### Production Build

Deploy contract to Rinkeby test network:

You need some eth in your address.

```
truffle migrate --reset --network=rinkeby
```

To compile clojurescript to javascript:

```
lein do clean, build
```

Then upload resources/public to heroku/IPFS.
