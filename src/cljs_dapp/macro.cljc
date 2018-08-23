(ns cljs-dapp.macro
  (:refer-clojure :exclude [slurp]))

(defmacro slurp [file]
  #?(:clj (clojure.core/slurp file)))
