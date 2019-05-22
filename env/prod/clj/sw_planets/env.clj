(ns sw-planets.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[sw-planets started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[sw-planets has shut down successfully]=-"))
   :middleware identity})
