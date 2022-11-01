##bin/sh
#MIGRATION_LABEL = "to-be-changed"
#DATE_WITH_TIME = "$(date +"%Y-%m-%d-%H:%M:%S")"
#
#	./mvnw liquibase:diff -DdiffChangeLogFile=src/main/resources/db/changelog/changes/${DATE_WITH_TIME}-${MIGRATION_LABEL}.yaml
##	@echo "  - include:" >> src/main/resources/db/changelog/db.changelog-master.yaml
##	@echo "      file: classpath*:db/changelog/changes/$(DATE_WITH_TIME)-$(MIGRATION_LABEL).yaml" >> src/main/resources/db/changelog/db.changelog-master.yaml