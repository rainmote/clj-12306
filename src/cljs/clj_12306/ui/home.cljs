(ns clj-12306.ui.home
  (:require [clj-12306.ui.base :as base]
            [antizer.reagent :as ant]))

(defn home-page []
  [ant/row {:gutter 16}
   [ant/col {:span 8}
    [ant/card {:title "任务总数量" :bordered false}
     [:h1 {} "8"]]]])
