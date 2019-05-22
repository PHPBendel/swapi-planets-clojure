(ns sw-planets.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [sw-planets.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[sw-planets started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[sw-planets has shut down successfully]=-"))
   :middleware wrap-dev})
