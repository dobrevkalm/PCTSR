/**
 * Running this program will combine each company from 'companyInformation.csv' with their distance array.
 * The distance array shows the distance to all other companies. The company's entry in the CSV represents its position in the array.
 * 
 * Run "node formatData > [resultFile].txt" to get a text file with the arrays
 */

const fs = require('fs');
// JS needs an empty string added when reading file cause it's JS
const jsonFile = fs.readFileSync('distances.json') + "";
const jsonData = JSON.parse(jsonFile);
const distances = jsonData.distances;

const companies = fs.readFileSync('../companies/companyInformation.csv') + "";
const companiesArray = companies.split("\n");

// give ot half a second just to make sure it reads the data first
setTimeout(() => {
    // we skip companiesArray[0] because it's the header row of the CSV
    for(var i = 1; i < companiesArray.length; i++) {
        // replace all commas so we can split on ";" in java
        const company = companiesArray[i].replace(/,/g, ";");
        // distances start at 0
        const distanceIdx = i-1;
        // remove the array's square brackets
        const distanceArray = JSON.stringify(distances[distanceIdx]).replace(/[\[\]]/g, "");
        // this will output a single line string for each company
        // we will have company;address;profit;latitude;longitude;distances
        console.log(`${company};${distanceArray}`);
    }
}, 500);