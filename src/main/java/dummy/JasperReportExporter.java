package dummy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRProperties;
import org.opennms.netmgt.jasper.jrobin.JRobinQueryExecutorFactory;

public class JasperReportExporter {

    private static final String REPORT_ROOT = "/home/mvrueden/dev/jasper-report-helper/src/main/resources/report-templates";

    private static final String RRD_ROOT = "/home/mvrueden/dev/opennms/target/opennms-15.0.0-SNAPSHOT/share/rrd";

    private static final String OUTPUT_TARGET = "/home/mvrueden/dev/jasper-report-helper/output";

    private static final String DEFAULT_START_TIME = "2014-11-03 00:00:00";

    private static final String DEFAULT_END_TIME = "2014-12-01 00:00:00";

    private static final Double DEFAULT_AVAILABILITY_WARNING = 98.0d;

    private static final Double DEFAULT_AVAILABILITY_CRITICAL = 97.0d;

    private static final Integer DEFAULT_TIME_RANGE_NUMBER = 7;

    private static final String DEFAULT_CATEGORY = "Development";

    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private static class ReportDescriptor {

        private final File reportFile;

        private final Map<String, Object> parameters = new HashMap<>();

        private ReportDescriptor(String reportName) {
            reportFile = new File(REPORT_ROOT, reportName);
        }

        public File getReportFile() {
            return reportFile;
        }

        public <T> ReportDescriptor param(String paramName, T paramValue) {
            parameters.put(paramName, paramValue);
            return this;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public String getTargetFile(String suffix) {
            return getReportFile().getName().replace(".jrxml", suffix);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, JRException, SQLException, ParseException {
        new File(OUTPUT_TARGET).mkdirs();
        JRProperties.setProperty(JRQueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + "jrobin", JRobinQueryExecutorFactory.class.getName());

        compileSubreport("AvailabilitySummaryChart_subreport.jrxml");
        compileSubreport("Top25PercentDown_subreport.jrxml");
        compileSubreport("PeakTraffic_subreport.jrxml");
        compileSubreport("DiskUsageForCTXServers_subreport1.jrxml");
        compileSubreport("InterfaceAvailabilityReport_subreport1.jrxml");
        compileSubreport("ResponseTime_subreport1.jrxml");
        compileSubreport("ResponseTimeSummary_Availability_subreport.jrxml");
        compileSubreport("ResponseTimeAvg_subreport.jrxml");
        compileSubreport("ResponseTimeSummary_subreport.jrxml");
        compileSubreport("ResponseTimeSummary_Response_Offenders_subreport.jrxml");
        compileSubreport("ResponseTimeSummary_Availability_Offenders_subreport.jrxml");
        compileSubreport("NodeId_to_NodeLabel_subreport.jrxml");
        compileSubreport("95thPercentileTrafficRate_subreport.jrxml");
        compileSubreport("TotalBytesTransferredByInterface_subreport1.jrxml");

        createReportsWithData();
//        createReportsWithoutData();
    }

    private static void createReportsWithoutData() throws ClassNotFoundException, FileNotFoundException, JRException, SQLException, ParseException {
        createReport(new ReportDescriptor("AssetManagementMaintExpired.jrxml")
                .param("DATE_FORMAT", "yyyy-MM-DD")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("AssetManagementMaintStrategy.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("AvailabilitySummary.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("AveragePeakTrafficRates.jrxml")
                .param("SURVEILLANCE_CATEGORY", "I DO NOT EXIST")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("DiskUsageForCTX.jrxml")
                .param("SURVEILLANCE_CATEGORY", "I DO NOT EXIST")
                .param("nameFilter", "%")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
//        createReport(new ReportDescriptor("Early-Morning-Report.jrxml")
//                .param("ONMS_REPORT_DIR", REPORT_ROOT));
//        createReport(new ReportDescriptor("EventAnalysis.jrxml")
//                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("InterfaceAvailabilityReport.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("NodeAvailabilityReport.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("ResponseTime.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("ResponseTimeCharts.jrxml")
                .param("SURVEILLANCE_CATEGORY", "I DO NOT EXIST")
//                .param("startDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
//                .param("endDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_END_TIME))
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("ResponseTimeSummary.jrxml")
                .param("SURVEILLANCE_CATEGORY", "I DO NOT EXIST")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("sample-report.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("SerialInterfaceUtilizationSummary.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("SnmpInterfaceOperAvailabilityReport.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("TotalBytesTransferredByInterface.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT)
                .param("ONMS_RRD_DIR", RRD_ROOT));
    }

    private static void createReportsWithData() throws ClassNotFoundException, FileNotFoundException, JRException, SQLException, ParseException {
        createReport(new ReportDescriptor("AssetManagementMaintExpired.jrxml")
                .param("DISPLAY_EXPIRED", "true")
                .param("WARNING_THRESHOLD_DAYS", 100)
                .param("DATE_FORMAT", "yyyy-MM-DD")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("AssetManagementMaintStrategy.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("AvailabilitySummary.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT)
                .param("START_TIME", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("TIME_RANGE", DEFAULT_TIME_RANGE_NUMBER));
        createReport(new ReportDescriptor("AveragePeakTrafficRates.jrxml")
                .param("startDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("endDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_END_TIME))
                .param("SURVEILLANCE_CATEGORY", DEFAULT_CATEGORY)
                .param("rrdDir", RRD_ROOT + "/snmp")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("DiskUsageForCTX.jrxml")
                .param("SURVEILLANCE_CATEGORY", DEFAULT_CATEGORY)
                .param("nameFilter", "%")
                .param("startDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("endDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_END_TIME))
                .param("rrdDir", RRD_ROOT + "/snmp")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("Early-Morning-Report.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("EventAnalysis.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("InterfaceAvailabilityReport.jrxml")
                .param("AVAILABILITY_WARNING", DEFAULT_AVAILABILITY_WARNING)
                .param("AVAILABILITY_CRITICAL", DEFAULT_AVAILABILITY_CRITICAL)
                .param("TIME_RANGE_NUMBER", DEFAULT_TIME_RANGE_NUMBER)
                .param("START_TIME", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("NodeAvailabilityReport.jrxml")
                .param("SURVEILLANCE_CATEGORY", DEFAULT_CATEGORY)
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("ResponseTime.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT)
                .param("rrdDir", RRD_ROOT));
        createReport(new ReportDescriptor("ResponseTimeCharts.jrxml")
                .param("SURVEILLANCE_CATEGORY", DEFAULT_CATEGORY)
                .param("startDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("endDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_END_TIME))
                .param("rrdDir", RRD_ROOT)
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("ResponseTimeSummary.jrxml")
                .param("startDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("TIME_RANGE", DEFAULT_TIME_RANGE_NUMBER)
                .param("SURVEILLANCE_CATEGORY", DEFAULT_CATEGORY)
                .param("rrdDir", RRD_ROOT)
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("sample-report.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
        createReport(new ReportDescriptor("SerialInterfaceUtilizationSummary.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT)
                .param("rrdDir", RRD_ROOT + "/snmp")
                .param("START_TIME", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("TIME_RANGE_NUMBER", DEFAULT_TIME_RANGE_NUMBER));
        createReport(new ReportDescriptor("SnmpInterfaceOperAvailabilityReport.jrxml")
                .param("ONMS_REPORT_DIR", REPORT_ROOT)
                .param("START_TIME", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("TIME_RANGE_NUMBER", DEFAULT_TIME_RANGE_NUMBER));
        createReport(new ReportDescriptor("TotalBytesTransferredByInterface.jrxml")
                .param("SURVEILLANCE_CATEGORY", "%")
                .param("startDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_START_TIME))
                .param("endDate", DEFAULT_DATE_FORMAT.parse(DEFAULT_END_TIME))
                .param("rrdDir", RRD_ROOT + "/snmp")
                .param("ONMS_REPORT_DIR", REPORT_ROOT));
    }


    private static void compileSubreport(String subreportName) throws JRException {
        System.out.print("-- Compiling subreport " + subreportName + "...");

        File input = Paths.get(REPORT_ROOT, "subreports", subreportName).toFile();
        File output = new File(input.toString().replace(".jrxml", ".jasper"));
        JasperCompileManager.compileReportToFile(input.toString(), output.toString());

        System.out.println(" finished");
        System.out.println();
    }

    public static void createReport(ReportDescriptor reportDescriptor) throws FileNotFoundException, JRException, ClassNotFoundException, SQLException {
        System.out.print("-- Compiling report " + reportDescriptor.getReportFile().getName() + "...");
        JasperReport report = JasperCompileManager.compileReport(new FileInputStream(reportDescriptor.getReportFile()));
        System.out.println(" finished");

//        final Map<String, Object> jrReportParms = new HashMap<>();
        printJRParameters(report.getParameters());
//        final Map<String, Object> jrReportParms = ;
//        buildSubreport(report);
//        jrReportParms.putAll(buildSubreport(new File(""), jasperReport));


        Class.forName("org.postgresql.Driver");
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/opennms", "opennms", "opennms")) {
            final JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportDescriptor.getParameters(), connection);

            JasperExportManager.exportReportToPdfFile(jasperPrint, Paths.get(OUTPUT_TARGET, reportDescriptor.getTargetFile(".pdf")).toString());
//            JasperExportManager.exportReportToHtmlFile(jasperPrint, Paths.get(OUTPUT_TARGET, reportDescriptor.getTargetFile(".html")).toString());
//
//            JRCsvExporter exporter = new JRCsvExporter();
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,  Paths.get(OUTPUT_TARGET, reportDescriptor.getTargetFile(".csv")).toString());
//            exporter.exportReport();
        }
        System.out.println();
    }

    private static void printJRParameters(final JRParameter[] reportParms) {
        System.out.println("Parameters: ");
        for (JRParameter eachParameter : reportParms) {
            if (!eachParameter.isSystemDefined() && eachParameter.isForPrompting()) {
                System.out.print("   " + eachParameter.getName() + " " + eachParameter.getValueClassName() + " " + eachParameter.getDescription());
                if (eachParameter.getDefaultValueExpression() != null) {
                    System.out.print(" " + eachParameter.getDefaultValueExpression().getText());
                }
                System.out.println();
            }
        }
    }

//    private static Map<String, Object> buildSubreport(final JasperReport mainReport) {
//        Map<String, Object> subreportMap = new HashMap<String, Object>();
//
//        // Filter parameter for sub reports
//        for (JRParameter parameter : mainReport.getParameters()) {
//            // We need only parameter for Sub reports and we *DON'T* need the default parameter JASPER_REPORT
//            if ("net.sf.jasperreports.engine.JasperReport".equals(parameter.getValueClassName()) && !"JASPER_REPORT".equals(parameter.getName())) {
//                subreportMap.put(parameter.getName(), parameter.getValueClassName());
//            }
//        }
//
////        for (final Map.Entry<String,Object> entry : subreportMap.entrySet()) {
////            try {
////                entry.setValue(JasperCompileManager.compileReport(mainReport.getgetm_globalReportRepository.getTemplateStream(reportId)));
////            } catch (final JRException e) {
////                LOG.debug("failed to compile report {}", reportId, e);
////            }
////        }
////
////        for (final Map.Entry<String,Object> entry : subreportMap.entrySet()) {
////            LOG.debug("Key: {} - Value: {}", entry.getKey(), entry.getValue());
////        }
//        return subreportMap;
//    }


}

