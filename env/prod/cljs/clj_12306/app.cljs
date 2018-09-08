(ns clj-12306.app
  (:require [clj-12306.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
