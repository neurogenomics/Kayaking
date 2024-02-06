import pygrib

# Specify the path to your GRIB file
grib_file_path = 'path/to/your/file.grib'

# Open the GRIB file
grbs = pygrib.open(grib_file_path)

# Iterate through the messages in the file
for grb in grbs:
    # Access information from the GRIB message
    print(grb)

# Close the GRIB file
grbs.close()
