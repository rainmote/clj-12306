(ns clj-12306.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [clj-12306.core-test]))

(doo-tests 'clj-12306.core-test)

