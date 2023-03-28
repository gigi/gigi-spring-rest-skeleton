.DEFAULT_GOAL := help
COMPOSE=docker-compose

.PHONY:help
help:
	@cat $(MAKEFILE_LIST) | grep -e "^[a-zA-Z_\-]*: *.*## *" | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: up
up: ## Create and start containers for local development
	$(COMPOSE) up

.PHONY: down
down: ## Stop and remove local Docker containers
	$(COMPOSE) down

.PHONY: run
run: ## Create and start local docker postgres container and application from source code
	$(COMPOSE) up -d postgres && ./gradlew bootRun --args='--spring.profiles.active=dev,sqldebug'

.PHONY: test
test: ## Run tests
	./gradlew test

.PHONY: coverage
coverage: ## Run tests with html coverage report in build/reports/jacoco/test/html/index.html
	./gradlew jacocoTestReport

.PHONY:migration
migration: ## Generate migration for current state
	./gradlew diffChangelog

.PHONY:docker
docker: ## Build dockerized application
	./gradlew clean bootJar && docker build -t gigi/gigi-spring-rest-skeleton:latest .
