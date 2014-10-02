The jasper-report-helper is a project to help you generate all reports for OpenNMS in hopefully an easy way without using the OpenNMS UI.


1. Copy all reports and subreports from OpenNMS to src/main/resources/report-templates. 
The directory structure should be the same as in OpenNMS.
Please only copy the source files.

2. Open the JasperReportExplorer.java file and edit the DEFAULT values (e.g. ONMS_REPORT_DIR) to point to your report-templates.
This should be the location of the src/main/resources/report-templates directory from step 1.

3. You may have to edit the JasperReportExplorer.java file to generate more or less reports.
You may also have to add/remove parameters which are currently set.

4. Run JasperReportExporter.main()