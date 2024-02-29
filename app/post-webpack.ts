// postbuild.js

const fs = require('fs');
const path = require('path');

// Function to modify the HTML content
function modifyHTMLFile(filePath) {
    const htmlPath = path.resolve(__dirname, filePath);

    // Read the HTML file
    fs.readFile(htmlPath, 'utf8', (err, data) => {
        if (err) {
            console.error(`Error reading file: ${err}`);
            return;
        }

        // Modify the HTML content
        const modifiedContent = data.replace('</html>', '<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyABvOAizOXHC2NsifAxfIyo-yN0W24_lyk" async></script></html>');

        // Write the modified content back to the file
        fs.writeFile(htmlPath, modifiedContent, 'utf8', (err) => {
            if (err) {
                console.error(`Error writing file: ${err}`);
                return;
            }
            console.log(`File ${filePath} has been modified successfully.`);
        });
    });
}

// Call the function to modify the HTML file
modifyHTMLFile('web-build/index.html');
