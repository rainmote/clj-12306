(ns clj-12306.graphql
  (:require [re-graph.core :as rg]
            [re-frame.core :as rf]))

(rf/reg-event-fx
 :re-graphql-init
 (fn [_ _]
   (rg/init {:http-url "api/graphql"})))
