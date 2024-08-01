#
# Copyright Terracotta, Inc.
# Copyright Super iPaaS Integration LLC, an IBM Company 2024
#
import java.lang.System
import terracotta

webappDir = sys.argv[0]
print "Got webapp.dir: " + webappDir
appUtil = terracotta.AppUtil(AdminApp, webappDir)
appUtil.installAll()

if AdminConfig.hasChanges():
    AdminConfig.save()
