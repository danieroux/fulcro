tests:
	yarn
	npx shadow-cljs -A:dev compile ci-tests
	npx karma start --single-run
	clojure -A:dev:test:clj-tests -J-Dguardrails.config=guardrails-test.edn -J-Dguardrails.enabled

dev:
	clojure -A:dev:test:clj-tests -J-Dguardrails.config=guardrails-test.edn -J-Dguardrails.enabled --watch --fail-fast --no-capture-output

workspaces:
	@echo "Workspaces will be on: http://localhost:9002"
	# flow-storm.storm-preload has to be first. Specifying [:devtools :preloads] in shadow-cljs wins, because
	# merge is left to right
	npx shadow-cljs -A:dev:workspaces watch workspaces \
	   --config-merge '{:preloads [com.fulcrologic.devtools.chrome-preload]}'

workspaces-with-flow-storm:
	@echo "Workspaces will be on: http://localhost:9002 and can be attached to with flow-storm-debugger-on-workspaces"
	# flow-storm.storm-preload has to be first. Specifying [:devtools :preloads] in shadow-cljs wins, because
	# merge is left to right
	npx shadow-cljs -A:dev:workspaces:with-flow-storm watch workspaces \
	   --config-merge '{:devtools {:preloads [flow-storm.storm-preload com.fulcrologic.devtools.chrome-preload]}}'

flow-storm-debugger-on-workspaces:
	@echo "Assumes 'make workspaces-with-flow-storm' is running in a separate process"
	clojure \
        -T:flow-storm-start-debugger \
        flow-storm.debugger.main/start-debugger \
        :build-id :workspaces \
        :port `cat .shadow-cljs/nrepl.port`

deploy:
	rm -rf target
	mvn deploy
