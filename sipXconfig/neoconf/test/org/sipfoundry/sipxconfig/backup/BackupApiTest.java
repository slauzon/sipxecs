package org.sipfoundry.sipxconfig.backup;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sipfoundry.sipxconfig.common.ScheduledDay;
import org.sipfoundry.sipxconfig.common.TimeOfDay;
import org.sipfoundry.sipxconfig.test.TestHelper;

public class BackupApiTest {

    @Test
    public void test() throws IOException {
        BackupApi api = new BackupApi();
        BackupSettings settings = new BackupSettings();
        BackupDbSettings backupDbSettings = new BackupDbSettings();
        backupDbSettings.setSettings(TestHelper.loadSettings("backup/backup-db.xml"));
        settings.setBackupDbSettings(backupDbSettings);
        settings.setSettings(TestHelper.loadSettings("backup/backup.xml"));
        settings.setModelFilesContext(TestHelper.getModelFilesContext(TestHelper.getSystemEtcDir()));
        BackupPlan plan = new BackupPlan();
        Map<String,String> archiveIds = new HashMap<String, String>();
        archiveIds.put("A", "Alpha");
        archiveIds.put("B", "Beta");
        archiveIds.put("C", "Gamma");
        plan.setType(BackupType.local);
        plan.setLimitedCount(10);
        DailyBackupSchedule s1 = new DailyBackupSchedule();
        s1.setBackupPlan(plan);
        s1.setScheduledDay(ScheduledDay.FRIDAY);
        s1.setTimeOfDay(new TimeOfDay(1, 2));
        plan.setEncodedDefinitionString("A,BEE,C");
        plan.setSchedules(Arrays.asList(s1));
        StringWriter actual = new StringWriter();

        //test default value different than actual value
        settings.getDb().getSetting("includeDeviceFiles").setTypedValue(true);
        assertTrue(settings.getIncludeDeviceFiles().getDefaultValue().equals("0"));
        assertTrue(settings.getIncludeDeviceFiles().getValue().equals("1"));

        //test default value different than actual value
        String tmpTest = "/var/sipxdata/tmp";
        assertTrue(settings.getTmpDir().equals(tmpTest));

        Map<String, List<String>> backups = new HashMap<String, List<String>>();
        backups.put("x", Arrays.asList("one", "two", "three"));
        api.writeBackup(actual, false, plan, backups, settings, archiveIds);
        String expected = IOUtils.toString(getClass().getResourceAsStream("expected.json"));
        TestHelper.assertEqualJson2(expected, actual.toString());
    }
}
