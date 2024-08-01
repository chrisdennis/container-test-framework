#
# Copyright Terracotta, Inc.
# Copyright Super iPaaS Integration LLC, an IBM Company 2024
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.#
import java.lang.Boolean
import terracotta

dso = terracotta.DSO(AdminTask)
dso.enable()

s1 = AdminConfig.getid('/Server:server1/')
SM = AdminConfig.list( 'SessionManager', s1 )
smAttr = [ [ 'enableUrlRewriting', 'true' ] ]

print 'SessionManager before:'
print AdminConfig.show( SM )
AdminConfig.modify( SM, smAttr )
print 'SessionManager  after:'
print AdminConfig.show( SM )
AdminConfig.save()
        
if AdminConfig.hasChanges():
    AdminConfig.save()
