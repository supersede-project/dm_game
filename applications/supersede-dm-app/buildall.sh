cd supersede-dm-datastore
echo -e "\nBuilding supersede-dm-datastore...\n"
./gradlew build

cd ../supersede-dm-core
echo -e "\nBuilding supersede-dm-core...\n"
./gradlew build

cd ../supersede-dm-integration
echo -e "\nBuilding supersede-dm-integration...\n"
./gradlew build

cd ../supersede-dm-executor
echo -e "\nBuilding supersede-dm-executor...\n"
./gradlew build

cd ../supersede-dm-ahprp
echo -e "\nBuilding supersede-dm-ahprp...\n"
./gradlew build

cd ../supersede-dm-ahprp-ui
echo -e "\nBuilding supersede-dm-ahprp-ui...\n"
./gradlew build

cd ../supersede-dm-garp
echo -e "\nBuilding supersede-dm-garp...\n"
./gradlew build

cd ../supersede-dm-garp-ui
echo -e "\nBuilding supersede-dm-garp-ui...\n"
./gradlew build

cd ../supersede-dm-depcheck
echo -e "\nBuilding supersede-dm-depcheck...\n"
gradle build

cd ../supersede-dm-depcheck-ui
echo -e "\nBuilding supersede-dm-depcheck-ui...\n"
gradle build

cd ../supersede-dm-planning-jmetal
echo -e "\nBuilding supersede-dm-planning-jmetal...\n"
./gradlew build

cd ../supersede-dm-similarity-gradle
echo -e "\nBuilding supersede-dm-similarity-gradle...\n"
./gradlew build -x test

#cd ../supersede-dm-orchestrator
#echo -e "\nBuilding supersede-dm-orchestrator...\n"
#./gradlew build

cd ../
echo -e "\nBuilding supersede-dm-app...\n"
./gradlew build