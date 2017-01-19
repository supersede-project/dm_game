echo -e "\nCleaning supersede-dm-app...\n"
gradle clean

cd supersede-dm-core
echo -e "\nBuilding supersede-dm-core...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-datastore
echo -e "\nBuilding supersede-dm-datastore...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-integration
echo -e "\nBuilding supersede-dm-integration...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-executor
echo -e "\nBuilding supersede-dm-executor...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-ahprp
echo -e "\nBuilding supersede-dm-ahprp...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-ahprp-ui
echo -e "\nBuilding supersede-dm-ahprp-ui...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-garp
echo -e "\nBuilding supersede-dm-garp...\n"
gradle build --refresh-dependencies -x test

cd ../supersede-dm-garp-ui
echo -e "\nBuilding supersede-dm-garp-ui...\n"
gradle build --refresh-dependencies -x test

cd ../supersede-dm-depcheck
echo -e "\nBuilding supersede-dm-depcheck...\n"
gradle build --refresh-dependencies -x test

cd ../supersede-dm-depcheck-ui
echo -e "\nBuilding supersede-dm-depcheck-ui...\n"
gradle build --refresh-dependencies -x test

cd ../supersede-dm-planning-jmetal
echo -e "\nBuilding supersede-dm-planning-jmetal...\n"
gradle build --refresh-dependencies

cd ../supersede-dm-orchestrator
echo -e "\nBuilding supersede-dm-orchestrator...\n"
gradle build --refresh-dependencies

cd ../
echo -e "\nBuilding supersede-dm-app...\n"
gradle build --refresh-dependencies -x test