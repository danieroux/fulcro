tests:
	yarn
	npx shadow-cljs -A:dev compile ci-tests
	npx karma start --single-run
	clojure -A:dev:test:clj-tests -J-Dguardrails.config=guardrails-test.edn -J-Dguardrails.enabled

dev:
	clojure -A:dev:test:clj-tests -J-Dguardrails.config=guardrails-test.edn -J-Dguardrails.enabled --watch --fail-fast --no-capture-output

workspaces:
	@echo "Workspaces will be on: http://localhost:9002"
	npx shadow-cljs -A:dev:workspaces watch workspaces

deploy:
	rm -rf target
	mvn deploy

check-clj-doc:
	clojure -T:build jar
	clojure -T:check-clj-doc analyze-local

playwright-install-chromium:
	npx playwright install chromium

playwright-run-tests:
	@echo "Only one test right now, especially to trip a router bug"
	@echo "Expects that 'make playwright-install-chromium' has been run"
	npx playwright test src/playwright-test/router-bug-test.spec.js --headed --timeout 0 --trace retain-on-failure
