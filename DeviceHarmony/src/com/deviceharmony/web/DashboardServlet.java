// DashboardServlet.java
// Location: src/com/deviceharmony/web/DashboardServlet.java
package com.deviceharmony.web;

import javax.servlet.http.*;
import java.io.IOException;

public class DashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(getHTML());
    }

    private String getHTML() {
        return "<!DOCTYPE html><html lang='en'><head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
            "<title>DeviceHarmony</title>" +
            "<style>" + getCSS() + "</style>" +
            "</head><body class='dark'>" +
            getBodyHTML() +
            "<script>" + getJS() + "</script>" +
            "</body></html>";
    }

    // ─── CSS ────────────────────────────────────────────────────────────────────

    private String getCSS() {
        return
            // Design tokens
            ":root{--bg:#0f172a;--surface:#1e293b;--s2:#334155;--s3:#475569;" +
            "--primary:#667eea;--pri-d:#5568d3;--success:#10b981;--warn:#f59e0b;" +
            "--danger:#ef4444;--text:#e2e8f0;--muted:#94a3b8;--faint:#64748b;" +
            "--border:#334155;--r:8px;--rl:12px;--sh:0 4px 6px rgba(0,0,0,.3)}" +
            "body.light{--bg:#f1f5f9;--surface:#fff;--s2:#f8fafc;--s3:#e2e8f0;" +
            "--text:#1e293b;--muted:#64748b;--faint:#94a3b8;--border:#e2e8f0;--sh:0 4px 6px rgba(0,0,0,.08)}" +
            // Reset and base
            "*{margin:0;padding:0;box-sizing:border-box}" +
            "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;" +
            "background:var(--bg);color:var(--text);min-height:100vh;transition:background .3s,color .3s}" +
            // Header
            ".hdr{position:sticky;top:0;z-index:100;background:linear-gradient(135deg,#667eea,#764ba2);" +
            "padding:.55rem 1.25rem;display:flex;align-items:center;gap:.75rem;box-shadow:var(--sh)}" +
            ".hdr-brand{font-size:1.15rem;font-weight:700;color:#fff;white-space:nowrap}" +
            ".hdr-ctr{background:rgba(255,255,255,.2);color:#fff;padding:.22rem .7rem;" +
            "border-radius:20px;font-size:.8rem;white-space:nowrap}" +
            ".hdr-space{flex:1}" +
            ".hdr-acts{display:flex;gap:.5rem;align-items:center}" +
            ".btn-theme{background:rgba(255,255,255,.2);color:#fff;border:none;" +
            "padding:.32rem .6rem;border-radius:6px;cursor:pointer;font-size:.95rem;transition:background .2s}" +
            ".btn-theme:hover{background:rgba(255,255,255,.35)}" +
            // Layout
            ".layout{display:flex;height:calc(100vh - 46px);overflow:hidden}" +
            // Sidebar
            ".sidebar{width:300px;flex:0 0 300px;background:var(--surface);" +
            "border-right:1px solid var(--border);display:flex;flex-direction:column;overflow:hidden}" +
            ".sb-hdr{padding:.65rem 1rem;border-bottom:1px solid var(--border);" +
            "font-weight:600;color:var(--muted);font-size:.76rem;text-transform:uppercase;letter-spacing:.05em}" +
            ".sb-scroll{flex:1;overflow-y:auto;padding:.45rem}" +
            // Device cards
            ".dc{background:var(--s2);border-radius:var(--r);padding:.7rem;" +
            "margin-bottom:.45rem;cursor:pointer;transition:all .2s;border:2px solid transparent}" +
            ".dc:hover{background:var(--s3)}" +
            ".dc.sel{border-color:var(--primary)}" +
            ".dc.pri-dev{border-color:var(--warn)}" +
            ".dc-top{display:flex;align-items:center;gap:.45rem;margin-bottom:.3rem}" +
            ".dc-ico{font-size:1.15rem;flex:0 0 auto}" +
            ".dc-nw{flex:1;min-width:0}" +
            ".dc-n{font-weight:600;font-size:.87rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}" +
            ".dc-s{font-size:.72rem;color:var(--muted);white-space:nowrap;overflow:hidden;text-overflow:ellipsis}" +
            ".dc-badges{display:flex;align-items:center;gap:.35rem;margin-bottom:.3rem;flex-wrap:wrap}" +
            ".dot{width:7px;height:7px;border-radius:50%;flex:0 0 7px}" +
            ".dot.online{background:var(--success);box-shadow:0 0 5px var(--success)}" +
            ".dot.offline{background:var(--danger)}" +
            ".badge{font-size:.66rem;font-weight:600;padding:.08rem .32rem;border-radius:3px;text-transform:uppercase}" +
            ".b-on{background:rgba(16,185,129,.2);color:var(--success)}" +
            ".b-off{background:rgba(239,68,68,.2);color:var(--danger)}" +
            ".b-agent{background:rgba(16,185,129,.15);color:var(--success)}" +
            ".b-share{background:rgba(245,158,11,.15);color:var(--warn)}" +
            ".b-manual{background:rgba(100,116,139,.15);color:var(--muted)}" +
            // Storage bar
            ".stor{margin-bottom:.3rem}" +
            ".stor-lbl{display:flex;justify-content:space-between;font-size:.68rem;color:var(--muted);margin-bottom:.12rem}" +
            ".stor-bar{width:100%;height:4px;background:var(--s3);border-radius:3px;overflow:hidden}" +
            ".stor-fill{height:100%;border-radius:3px;transition:width .4s}" +
            ".sg{background:var(--success)}.sa{background:var(--warn)}.sr{background:var(--danger)}" +
            // Device actions row
            ".dc-acts{display:flex;gap:.28rem;margin-top:.38rem;flex-wrap:wrap}" +
            // Main area
            ".main{flex:1;display:flex;flex-direction:column;overflow:hidden}" +
            // Tabs
            ".tabs{display:flex;border-bottom:2px solid var(--border);background:var(--surface);padding:0 1rem}" +
            ".tab-btn{padding:.65rem 1.1rem;background:none;border:none;color:var(--muted);" +
            "font-size:.9rem;cursor:pointer;border-bottom:2px solid transparent;margin-bottom:-2px;transition:all .2s}" +
            ".tab-btn.active{color:var(--primary);border-bottom-color:var(--primary);font-weight:600}" +
            ".tab-btn:hover:not(.active){color:var(--text)}" +
            ".tab-c{display:none;flex:1;overflow:hidden;flex-direction:column}" +
            ".tab-c.active{display:flex}" +
            // File toolbar
            ".ftbar{padding:.55rem .85rem;background:var(--surface);border-bottom:1px solid var(--border);" +
            "display:flex;align-items:center;gap:.55rem;flex-wrap:wrap}" +
            ".bc{display:flex;align-items:center;gap:.18rem;flex:1;min-width:80px;flex-wrap:wrap}" +
            ".bc-btn{background:none;border:none;color:var(--primary);cursor:pointer;" +
            "font-size:.82rem;padding:.12rem .32rem;border-radius:4px;transition:background .2s}" +
            ".bc-btn:hover{background:var(--s2)}" +
            ".bc-cur{font-size:.82rem;font-weight:600;color:var(--text);padding:.12rem .32rem}" +
            ".bc-sep{color:var(--faint);font-size:.72rem}" +
            ".srch{padding:.38rem .6rem;border:1px solid var(--border);border-radius:6px;" +
            "background:var(--s2);color:var(--text);font-size:.82rem;width:155px}" +
            ".srch:focus{outline:none;border-color:var(--primary)}" +
            ".sort-sel{padding:.38rem .6rem;border:1px solid var(--border);border-radius:6px;" +
            "background:var(--s2);color:var(--text);font-size:.8rem}" +
            ".vt{display:flex;gap:.18rem}" +
            ".vt-btn{padding:.32rem .52rem;border:1px solid var(--border);background:var(--s2);" +
            "color:var(--muted);border-radius:5px;cursor:pointer;transition:all .2s}" +
            ".vt-btn.active{background:var(--primary);color:#fff;border-color:var(--primary)}" +
            // File area
            ".f-area{flex:1;overflow-y:auto;padding:.8rem}" +
            ".f-empty{text-align:center;padding:2.5rem 1rem;color:var(--faint);font-size:.92rem}" +
            ".no-dev{display:flex;flex-direction:column;align-items:center;justify-content:center;" +
            "flex:1;color:var(--faint);text-align:center;padding:2rem}" +
            ".no-dev-ico{font-size:2.8rem;margin-bottom:.65rem}" +
            // Grid view
            ".f-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(115px,1fr));gap:.6rem}" +
            ".fg{background:var(--surface);border:1px solid var(--border);border-radius:var(--r);" +
            "padding:.65rem;text-align:center;cursor:pointer;transition:all .2s;position:relative;overflow:hidden}" +
            ".fg:hover{border-color:var(--primary);background:var(--s2)}" +
            ".fg-ico{font-size:1.9rem;margin-bottom:.28rem;display:block}" +
            ".fg-nm{font-size:.72rem;word-break:break-all;line-height:1.3;" +
            "display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;color:var(--text)}" +
            ".fg-ov{position:absolute;inset:0;background:rgba(102,126,234,.88);" +
            "display:flex;align-items:center;justify-content:center;opacity:0;transition:opacity .2s;border-radius:var(--r)}" +
            ".fg:hover .fg-ov{opacity:1}" +
            // List view
            ".f-tbl{width:100%;border-collapse:collapse}" +
            ".f-tbl th{text-align:left;padding:.42rem .65rem;color:var(--muted);font-size:.73rem;" +
            "font-weight:600;text-transform:uppercase;letter-spacing:.04em;border-bottom:1px solid var(--border)}" +
            ".f-tbl tr:not(:first-child):hover td{background:var(--surface)}" +
            ".f-tbl td{padding:.52rem .65rem;font-size:.83rem;border-bottom:1px solid var(--border)}" +
            ".fl-nm{display:flex;align-items:center;gap:.32rem;cursor:pointer;color:var(--text)}" +
            ".fl-nm:hover{color:var(--primary)}" +
            // Spinner
            ".spin-w{text-align:center;padding:2.5rem;color:var(--muted)}" +
            ".spin{display:inline-block;width:30px;height:30px;border:3px solid var(--s3);" +
            "border-top-color:var(--primary);border-radius:50%;animation:spin .8s linear infinite;margin-bottom:.55rem}" +
            "@keyframes spin{to{transform:rotate(360deg)}}" +
            // Logs
            ".logs-a{flex:1;overflow-y:auto;padding:.8rem}" +
            ".log-c{background:var(--surface);border-radius:var(--r);padding:.7rem .85rem;" +
            "margin-bottom:.45rem;border-left:4px solid var(--border);display:flex;gap:.6rem;align-items:flex-start}" +
            ".log-c.success{border-left-color:var(--success)}" +
            ".log-c.error{border-left-color:var(--danger)}" +
            ".log-c.pending{border-left-color:var(--warn)}" +
            ".log-ico{font-size:1.05rem;flex:0 0 auto;margin-top:.05rem}" +
            ".log-b{flex:1;min-width:0}" +
            ".log-ttl{font-weight:600;font-size:.85rem;margin-bottom:.15rem}" +
            ".log-det{font-size:.78rem;color:var(--muted)}" +
            ".log-err{font-size:.75rem;color:var(--danger);margin-top:.13rem}" +
            ".log-tm{font-size:.7rem;color:var(--faint);margin-top:.18rem}" +
            // Buttons
            ".btn{background:var(--primary);color:#fff;border:none;padding:.48rem .95rem;" +
            "border-radius:6px;cursor:pointer;font-size:.83rem;font-weight:500;transition:all .2s;white-space:nowrap}" +
            ".btn:hover{background:var(--pri-d);transform:translateY(-1px)}" +
            ".btn-sm{padding:.28rem .58rem;font-size:.76rem}" +
            ".btn-d{background:var(--danger)}.btn-d:hover{background:#dc2626}" +
            ".btn-ok{background:var(--success)}.btn-ok:hover{background:#059669}" +
            ".btn-g{background:transparent;color:var(--muted);border:1px solid var(--border)}" +
            ".btn-g:hover{background:var(--s2);color:var(--text);transform:none}" +
            // Modal
            ".modal{display:none;position:fixed;z-index:500;inset:0;" +
            "background:rgba(0,0,0,.7);backdrop-filter:blur(4px);align-items:center;justify-content:center}" +
            ".modal.open{display:flex}" +
            ".mbox{background:var(--surface);border-radius:var(--rl);padding:1.65rem;" +
            "width:min(530px,95vw);max-height:90vh;overflow-y:auto;" +
            "box-shadow:0 20px 40px rgba(0,0,0,.4);animation:mIn .2s ease}" +
            "@keyframes mIn{from{transform:translateY(-16px);opacity:0}to{transform:translateY(0);opacity:1}}" +
            ".mhdr{display:flex;justify-content:space-between;align-items:center;margin-bottom:1.15rem}" +
            ".mtit{font-size:1.1rem;font-weight:700}" +
            ".mcls{background:none;border:none;color:var(--muted);font-size:1.35rem;" +
            "cursor:pointer;line-height:1;padding:.18rem;transition:color .2s}" +
            ".mcls:hover{color:var(--text)}" +
            // Forms
            ".fg2{margin-bottom:.8rem}" +
            ".fl2{display:block;margin-bottom:.32rem;color:var(--muted);font-size:.82rem;font-weight:500}" +
            ".fi,.fs2{width:100%;padding:.58rem .75rem;border:1px solid var(--border);" +
            "border-radius:6px;background:var(--s2);color:var(--text);font-size:.9rem;transition:border-color .2s}" +
            ".fi:focus,.fs2:focus{outline:none;border-color:var(--primary)}" +
            ".frow{display:flex;gap:.6rem}.frow .fg2{flex:1}" +
            ".ffoot{display:flex;gap:.6rem;justify-content:flex-end;margin-top:1.15rem}" +
            // Upload drop zone
            ".dz{border:2px dashed var(--border);border-radius:var(--r);padding:1.6rem;" +
            "text-align:center;cursor:pointer;transition:all .2s;margin-bottom:.8rem}" +
            ".dz:hover,.dz.dov{border-color:var(--primary);background:rgba(102,126,234,.05)}" +
            ".dz-ico{font-size:2rem;margin-bottom:.35rem}" +
            ".dz-txt{color:var(--muted);font-size:.86rem}" +
            ".dz-txt span{color:var(--primary);cursor:pointer}" +
            ".uq{max-height:170px;overflow-y:auto;margin-bottom:.8rem}" +
            ".ui{display:flex;align-items:center;gap:.42rem;padding:.42rem .62rem;" +
            "background:var(--s2);border-radius:6px;margin-bottom:.32rem;font-size:.83rem}" +
            ".ui-nm{flex:1;min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}" +
            ".ui-sz{color:var(--muted);font-size:.74rem;white-space:nowrap}" +
            // Toast
            "#toast-c{position:fixed;bottom:1.15rem;right:1.15rem;z-index:9999;" +
            "display:flex;flex-direction:column;gap:.38rem;pointer-events:none}" +
            ".toast{background:var(--surface);color:var(--text);padding:.7rem .95rem;" +
            "border-radius:var(--r);box-shadow:0 4px 16px rgba(0,0,0,.35);" +
            "border-left:4px solid var(--border);max-width:290px;font-size:.85rem;" +
            "animation:tIn .3s ease;pointer-events:all;opacity:1;transition:opacity .3s}" +
            ".toast.fade-out{opacity:0}" +
            ".toast.success{border-left-color:var(--success)}" +
            ".toast.error{border-left-color:var(--danger)}" +
            ".toast.info{border-left-color:var(--primary)}" +
            "@keyframes tIn{from{transform:translateX(110%);opacity:0}to{transform:translateX(0);opacity:1}}" +
            // Scrollbars
            "::-webkit-scrollbar{width:6px;height:6px}" +
            "::-webkit-scrollbar-track{background:var(--bg)}" +
            "::-webkit-scrollbar-thumb{background:var(--s3);border-radius:3px}" +
            // Responsive
            "@media(max-width:768px){.sidebar{display:none}.srch{width:110px}}";
    }

    // ─── BODY HTML ──────────────────────────────────────────────────────────────

    private String getBodyHTML() {
        return
            // Header
            "<header class='hdr'>" +
            "<div class='hdr-brand'>&#128279; DeviceHarmony</div>" +
            "<span class='hdr-ctr' id='devCtr'>Loading&hellip;</span>" +
            "<div class='hdr-space'></div>" +
            "<div class='hdr-acts'>" +
            "<button class='btn-theme' onclick='toggleTheme()' id='themeBtn' title='Toggle theme'>&#127769;</button>" +
            "<button class='btn btn-sm' style='background:rgba(255,255,255,.25)' onclick='showAddDeviceModal()'>+ Add Device</button>" +
            "</div></header>" +
            // Two-column layout
            "<div class='layout'>" +
            // ── Sidebar ──
            "<div class='sidebar'>" +
            "<div class='sb-hdr'>Connected Devices</div>" +
            "<div class='sb-scroll' id='deviceList'><div class='f-empty'>Loading&hellip;</div></div>" +
            "</div>" +
            // ── Main ──
            "<div class='main'>" +
            "<div class='tabs'>" +
            "<button class='tab-btn active' id='tbf' onclick='switchTab(\"files\")'>&#128193; File Browser</button>" +
            "<button class='tab-btn' id='tbl' onclick='switchTab(\"logs\")'>&#128203; Transactions</button>" +
            "</div>" +
            // File Browser tab
            "<div class='tab-c active' id='tab-files'>" +
            "<div class='ftbar'>" +
            "<div class='bc' id='breadcrumb'><span style='color:var(--faint);font-size:.82rem'>Select a device</span></div>" +
            "<input type='text' class='srch' id='srchBox' placeholder='&#128269; Search&hellip;' oninput='renderFiles()' disabled>" +
            "<select class='sort-sel' id='sortSel' onchange='renderFiles()' disabled>" +
            "<option value='name'>Name &#8593;</option>" +
            "<option value='size'>Size</option>" +
            "<option value='date'>Date</option>" +
            "</select>" +
            "<div class='vt'>" +
            "<button class='vt-btn active' id='gvBtn' onclick='setView(\"grid\")' title='Grid'>&#8862;</button>" +
            "<button class='vt-btn' id='lvBtn' onclick='setView(\"list\")' title='List'>&#8801;</button>" +
            "</div>" +
            "<button class='btn btn-sm' id='upBtn' onclick='showUploadModal()' disabled>&#8679; Upload</button>" +
            "</div>" +
            "<div class='f-area' id='fileArea'>" +
            "<div class='no-dev'><div class='no-dev-ico'>&#128187;</div>" +
            "<p>Select a device from the sidebar to browse files</p></div>" +
            "</div>" +
            "</div>" +
            // Transactions tab
            "<div class='tab-c' id='tab-logs'>" +
            "<div class='logs-a' id='logList'><div class='f-empty'>No transactions yet</div></div>" +
            "</div>" +
            "</div>" +   // end .main
            "</div>" +   // end .layout
            // ── Add Device Modal ──
            "<div class='modal' id='addDevModal'>" +
            "<div class='mbox'>" +
            "<div class='mhdr'><span class='mtit'>&#10133; Add New Device</span>" +
            "<button class='mcls' onclick='closeAddDeviceModal()'>&#215;</button></div>" +
            "<div class='fg2'><label class='fl2'>Connection Type</label>" +
            "<select class='fs2' id='devType' onchange='updateDevForm()'>" +
            "<option value='agent'>&#129302; Agent (by IP)</option>" +
            "<option value='share'>&#127760; Network Share (SMB/NFS)</option>" +
            "<option value='manual'>&#128194; Manual Local Path</option>" +
            "</select></div>" +
            "<div class='fg2'><label class='fl2'>Device Name</label>" +
            "<input type='text' class='fi' id='devName' placeholder='My Laptop'></div>" +
            "<div id='agentFlds'>" +
            "<div class='frow'>" +
            "<div class='fg2'><label class='fl2'>IP Address</label>" +
            "<input type='text' class='fi' id='ipAddr' placeholder='192.168.1.100'></div>" +
            "<div class='fg2' style='max-width:88px'><label class='fl2'>Port</label>" +
            "<input type='number' class='fi' id='agentPort' value='9877'></div>" +
            "</div></div>" +
            "<div id='shareFlds' style='display:none'>" +
            "<div class='fg2'><label class='fl2'>Network Share Path</label>" +
            "<input type='text' class='fi' id='sharePath' placeholder='//192.168.1.100/Share'></div>" +
            "</div>" +
            "<div id='manualFlds' style='display:none'>" +
            "<div class='fg2'><label class='fl2'>Local Directory Path</label>" +
            "<input type='text' class='fi' id='localPath' placeholder='/home/user/Documents'></div>" +
            "</div>" +
            "<div class='ffoot'>" +
            "<button class='btn btn-g' onclick='closeAddDeviceModal()'>Cancel</button>" +
            "<button class='btn btn-ok' onclick='addDevice()'>Add Device</button>" +
            "</div></div></div>" +
            // ── Upload Modal ──
            "<div class='modal' id='uploadModal'>" +
            "<div class='mbox'>" +
            "<div class='mhdr'><span class='mtit'>&#8679; Upload Files</span>" +
            "<button class='mcls' onclick='closeUploadModal()'>&#215;</button></div>" +
            "<div class='dz' id='dropZone' onclick=\"document.getElementById('upFileIn').click()\">" +
            "<div class='dz-ico'>&#128194;</div>" +
            "<div class='dz-txt'>Drag &amp; drop files here or <span>click to browse</span></div>" +
            "</div>" +
            "<input type='file' id='upFileIn' multiple style='display:none' onchange='addFilesToQueue(this.files)'>" +
            "<div class='uq' id='uploadQueue'></div>" +
            "<div class='ffoot'>" +
            "<button class='btn btn-g' onclick='closeUploadModal()'>Cancel</button>" +
            "<button class='btn btn-ok' id='upAllBtn' onclick='uploadAll()' disabled>&#8679; Upload All</button>" +
            "</div></div></div>" +
            // Toast container
            "<div id='toast-c'></div>";
    }

    // ─── JavaScript ─────────────────────────────────────────────────────────────

    private String getJS() {
        return
            // ── State ──
            "var selectedDevice=null,currentPath='/',currentView='grid';" +
            "var deviceMap={},currentFiles=[],pendingFiles=[],bcPaths=[];" +

            // ── Utilities ──
            "function esc(s){if(!s)return '';return String(s)" +
            ".replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/\"/g,'&quot;');}" +

            "function fmtBytes(b){if(!b||b<=0)return '0 B';" +
            "var k=1024,s=['B','KB','MB','GB','TB'],i=Math.floor(Math.log(b)/Math.log(k));" +
            "return(b/Math.pow(k,i)).toFixed(1)+' '+s[i];}" +

            "function relTime(ts){if(!ts)return '';" +
            "var d=Date.now()-new Date(ts).getTime();" +
            "if(d<60000)return 'just now';" +
            "if(d<3600000)return Math.floor(d/60000)+'m ago';" +
            "if(d<86400000)return Math.floor(d/3600000)+'h ago';" +
            "return Math.floor(d/86400000)+'d ago';}" +

            // ── Theme toggle ──
            "function toggleTheme(){" +
            "var b=document.body,btn=document.getElementById('themeBtn');" +
            "if(b.classList.contains('dark')){b.classList.replace('dark','light');btn.innerHTML='&#9728;&#65039;';}" +
            "else{b.classList.replace('light','dark');btn.innerHTML='&#127769;';}}" +

            // ── Tab switching ──
            "function switchTab(t){" +
            "document.getElementById('tbf').classList.toggle('active',t==='files');" +
            "document.getElementById('tbl').classList.toggle('active',t==='logs');" +
            "document.querySelectorAll('.tab-c').forEach(function(c){c.classList.remove('active');});" +
            "document.getElementById('tab-'+t).classList.add('active');" +
            "if(t==='logs')loadLogs();}" +

            // ── Device icon helper ──
            "function devIcon(type,pri){" +
            "if(pri)return '&#11088;';" +
            "if(type==='agent')return '&#129302;';" +
            "if(type==='share')return '&#127760;';" +
            "if(type==='manual')return '&#128194;';" +
            "return '&#128187;';}" +

            // ── Load devices ──
            "function loadDevices(){" +
            "fetch('/api/devices').then(function(r){return r.json();}).then(function(data){" +
            "var on=data.filter(function(d){return d.status==='online';}).length;" +
            "document.getElementById('devCtr').textContent=on+'/'+data.length+' devices online';" +
            "deviceMap={};" +
            "data.forEach(function(d){deviceMap[d.device_id]=d;});" +
            "if(!data.length){" +
            "document.getElementById('deviceList').innerHTML=" +
            "'<div class=\"f-empty\" style=\"padding:1.2rem\">No devices<br>" +
            "<small style=\"color:var(--faint)\">Click + Add Device</small></div>';" +
            "return;}" +
            "var html=data.map(function(d){" +
            "var pct=d.total_storage>0?(d.total_storage-d.available_storage)/d.total_storage*100:-1;" +
            "var bc=pct<0?'':pct<70?'sg':pct<90?'sa':'sr';" +
            "var storLbl=d.total_storage>0?" +
            "fmtBytes(d.total_storage-d.available_storage)+' / '+fmtBytes(d.total_storage):'N/A';" +
            "var isSel=selectedDevice&&selectedDevice.device_id===d.device_id;" +
            "var sub=d.device_type==='agent'?(d.ip_address+':'+d.port):(d.share_path||'Local');" +
            "var stCls=d.status==='online'?'b-on':'b-off';" +
            "return '<div class=\"dc'+(isSel?' sel':'')+(d.is_primary?' pri-dev':'')+'\" data-id=\"'+d.device_id+'\">'+" +
            "'<div class=\"dc-top\"><span class=\"dc-ico\">'+devIcon(d.device_type,d.is_primary)+'</span>'+" +
            "'<div class=\"dc-nw\"><div class=\"dc-n\">'+esc(d.device_name)+'</div>'+" +
            "'<div class=\"dc-s\">'+esc(sub)+'</div></div></div>'+" +
            "'<div class=\"dc-badges\">'+" +
            "'<span class=\"dot '+d.status+'\"></span>'+" +
            "'<span class=\"badge '+stCls+'\">'+d.status+'</span>'+" +
            "'<span class=\"badge b-'+d.device_type+'\">'+d.device_type+'</span>'+" +
            "(d.is_primary?'<span class=\"badge\" style=\"background:rgba(245,158,11,.15);color:var(--warn)\">Primary</span>':'')+'</div>'+" +
            "(pct>=0?'<div class=\"stor\"><div class=\"stor-lbl\"><span>Storage</span><span>'+storLbl+'</span></div>'+" +
            "'<div class=\"stor-bar\"><div class=\"stor-fill '+bc+'\" style=\"width:'+Math.min(100,pct).toFixed(1)+'%\"></div></div></div>':'')+" +
            "'<div class=\"dc-acts\">'+" +
            "(!d.is_primary?'<button class=\"btn btn-sm btn-g\" data-action=\"primary\" data-id=\"'+d.device_id+'\">Set Primary</button>':'')+" +
            "'<button class=\"btn btn-sm btn-d\" data-action=\"delete\" data-id=\"'+d.device_id+'\">Remove</button>'+" +
            "'</div></div>';" +
            "}).join('');" +
            "document.getElementById('deviceList').innerHTML=html;" +
            "}).catch(function(e){console.error('Device load error:',e);});}" +

            // ── Event delegation for sidebar ──
            "document.getElementById('deviceList').addEventListener('click',function(e){" +
            "var btn=e.target.closest('[data-action]');" +
            "if(btn){e.stopPropagation();" +
            "if(btn.dataset.action==='primary')setPrimary(btn.dataset.id);" +
            "else if(btn.dataset.action==='delete')deleteDevice(btn.dataset.id);" +
            "return;}" +
            "var card=e.target.closest('.dc');" +
            "if(card&&deviceMap[card.dataset.id])selectDevice(deviceMap[card.dataset.id]);});" +

            // ── Select device ──
            "function selectDevice(device){" +
            "if(!device)return;" +
            "selectedDevice=device;currentPath='/';" +
            "document.getElementById('srchBox').disabled=false;" +
            "document.getElementById('sortSel').disabled=false;" +
            "document.getElementById('upBtn').disabled=false;" +
            "loadDevices();loadFiles();}" +

            // ── File type icons ──
            "var FICONS={pdf:'&#128213;',doc:'&#128221;',docx:'&#128221;',odt:'&#128221;'," +
            "xls:'&#128202;',xlsx:'&#128202;',csv:'&#128202;',ods:'&#128202;'," +
            "png:'&#128444;&#65039;',jpg:'&#128444;&#65039;',jpeg:'&#128444;&#65039;',gif:'&#128444;&#65039;'," +
            "svg:'&#128444;&#65039;',webp:'&#128444;&#65039;',bmp:'&#128444;&#65039;'," +
            "mp4:'&#127916;',avi:'&#127916;',mkv:'&#127916;',mov:'&#127916;',wmv:'&#127916;'," +
            "mp3:'&#127925;',wav:'&#127925;',ogg:'&#127925;',flac:'&#127925;',aac:'&#127925;'," +
            "zip:'&#128476;&#65039;',tar:'&#128476;&#65039;',gz:'&#128476;&#65039;',rar:'&#128476;&#65039;'," +
            "java:'&#9749;',py:'&#128013;',js:'&#127760;',ts:'&#127760;',html:'&#127760;',css:'&#127760;',php:'&#127760;'," +
            "c:'&#128220;',cpp:'&#128220;',h:'&#128220;',go:'&#128220;',rs:'&#128220;',rb:'&#128220;'," +
            "txt:'&#128196;',md:'&#128196;',log:'&#128196;',json:'&#128196;',xml:'&#128196;',yaml:'&#128196;',yml:'&#128196;'," +
            "sh:'&#9881;&#65039;',bat:'&#9881;&#65039;',exe:'&#9881;&#65039;'," +
            "ppt:'&#128202;',pptx:'&#128202;',iso:'&#128191;',dmg:'&#128191;',ttf:'&#128288;',woff:'&#128288;'};" +

            "function fileIcon(name,isDir){" +
            "if(isDir)return '&#128193;';" +
            "var ext=name.includes('.')?name.split('.').pop().toLowerCase():'';" +
            "return FICONS[ext]||'&#128196;';}" +

            // ── Load files ──
            "function loadFiles(){" +
            "if(!selectedDevice)return;" +
            "document.getElementById('fileArea').innerHTML=" +
            "'<div class=\"spin-w\"><div class=\"spin\"></div><div>Loading files&hellip;</div></div>';" +
            "updateBreadcrumb();" +
            "fetch('/api/files/list?device='+encodeURIComponent(selectedDevice.device_id)+'&path='+encodeURIComponent(currentPath))" +
            ".then(function(r){return r.json();})" +
            ".then(function(data){" +
            "if(data.error)throw new Error(data.error);" +
            "currentFiles=data;renderFiles();" +
            "}).catch(function(e){" +
            "document.getElementById('fileArea').innerHTML=" +
            "'<div class=\"f-empty\" style=\"color:var(--danger)\">&#9888; Error: '+esc(e.message)+'</div>';});}" +

            // ── Render files (search + sort applied) ──
            "function renderFiles(){" +
            "var search=document.getElementById('srchBox').value.toLowerCase();" +
            "var sort=document.getElementById('sortSel').value;" +
            "var files=currentFiles.filter(function(f){return!search||f.name.toLowerCase().includes(search);});" +
            "files.sort(function(a,b){" +
            "if(a.isDirectory!==b.isDirectory)return a.isDirectory?-1:1;" +
            "if(sort==='size')return(a.size||0)-(b.size||0);" +
            "if(sort==='date')return(b.lastModified||0)-(a.lastModified||0);" +
            "return a.name.localeCompare(b.name);});" +
            "if(!files.length){" +
            "document.getElementById('fileArea').innerHTML=" +
            "'<div class=\"f-empty\">'+(search?'&#128269; No matching files':'&#128228; Folder is empty')+'</div>';" +
            "return;}" +
            "if(currentView==='grid')renderGrid(files);else renderList(files);}" +

            // ── Grid view ──
            "function renderGrid(files){" +
            "var html=files.map(function(f,i){" +
            "var ico=fileIcon(f.name,f.isDirectory);" +
            "if(f.isDirectory){" +
            "return '<div class=\"fg\" onclick=\"openItem('+i+')\">'+  " +
            "'<span class=\"fg-ico\">'+ico+'</span>'+" +
            "'<div class=\"fg-nm\">'+esc(f.name)+'</div></div>';}" +
            "return '<div class=\"fg\">'+" +
            "'<span class=\"fg-ico\">'+ico+'</span>'+" +
            "'<div class=\"fg-nm\">'+esc(f.name)+'</div>'+" +
            "'<div class=\"fg-ov\"><button class=\"btn btn-sm\" onclick=\"openItem('+i+')\">&#8681; Download</button></div>'+" +
            "'</div>';}).join('');" +
            "document.getElementById('fileArea').innerHTML='<div class=\"f-grid\">'+html+'</div>';}" +

            // ── List view ──
            "function renderList(files){" +
            "var rows=files.map(function(f,i){" +
            "var ico=fileIcon(f.name,f.isDirectory);" +
            "return '<tr><td><span class=\"fl-nm\" onclick=\"openItem('+i+')\">'+ico+' '+esc(f.name)+'</span></td>'+" +
            "'<td style=\"color:var(--muted)\">'+(f.isDirectory?'&mdash;':fmtBytes(f.size))+'</td>'+" +
            "'<td style=\"color:var(--muted);font-size:.78rem\">'+relTime(f.lastModified)+'</td></tr>';}).join('');" +
            "document.getElementById('fileArea').innerHTML=" +
            "'<table class=\"f-tbl\"><thead><tr><th>Name</th><th>Size</th><th>Modified</th></tr></thead>'+" +
            "'<tbody>'+rows+'</tbody></table>';}" +

            // ── File item click dispatcher ──
            "function openItem(i){" +
            "var f=currentFiles[i];" +
            "if(f.isDirectory)openFolder(f.path);else downloadFile(f.path);}" +

            "function openFolder(path){currentPath=path;loadFiles();}" +

            "function navigateUp(){" +
            "var idx=currentPath.lastIndexOf('/');" +
            "currentPath=idx>0?currentPath.substring(0,idx):'/';loadFiles();}" +

            // ── Breadcrumb (index-based to avoid onclick escaping issues) ──
            "function updateBreadcrumb(){" +
            "if(!selectedDevice)return;" +
            "var parts=currentPath==='/'?[]:currentPath.split('/').filter(function(p){return p!=='';});" +
            "bcPaths=['/'];" +
            "var built='';" +
            "parts.forEach(function(p){built+='/'+p;bcPaths.push(built);});" +
            "var html='<button class=\"bc-btn\" onclick=\"navigateBc(0)\">'+esc(selectedDevice.device_name)+'</button>';" +
            "parts.forEach(function(p,i){" +
            "html+='<span class=\"bc-sep\">&#8250;</span>';" +
            "if(i===parts.length-1){html+='<span class=\"bc-cur\">'+esc(p)+'</span>';}" +
            "else{html+='<button class=\"bc-btn\" onclick=\"navigateBc('+(i+1)+')\">'+esc(p)+'</button>';}});" +
            "document.getElementById('breadcrumb').innerHTML=html;}" +

            "function navigateBc(i){navigateTo(bcPaths[i]);}" +
            "function navigateTo(path){currentPath=path;loadFiles();}" +

            // ── View toggle ──
            "function setView(v){" +
            "currentView=v;" +
            "document.getElementById('gvBtn').classList.toggle('active',v==='grid');" +
            "document.getElementById('lvBtn').classList.toggle('active',v==='list');" +
            "if(currentFiles.length)renderFiles();}" +

            // ── Load logs ──
            "function loadLogs(){" +
            "fetch('/api/logs').then(function(r){return r.json();}).then(function(data){" +
            "if(!data.length){" +
            "document.getElementById('logList').innerHTML='<div class=\"f-empty\">No transactions yet</div>';return;}" +
            "var html=data.map(function(l){" +
            "var ico=l.status==='success'?'&#9989;':l.status==='error'?'&#10060;':'&#9203;';" +
            "return '<div class=\"log-c '+l.status+'\">'+" +
            "'<span class=\"log-ico\">'+ico+'</span>'+" +
            "'<div class=\"log-b\">'+" +
            "'<div class=\"log-ttl\">'+esc(l.operation||'Operation')+(l.duration_ms?' ('+l.duration_ms+'ms)':'')+'</div>'+" +
            "'<div class=\"log-det\">'+esc(l.source_name||'N/A')+' &rarr; '+esc(l.target_name||'Server')+': '+" +
            "esc(l.file_name||'')+(l.file_size?' ('+fmtBytes(l.file_size)+')':'')+'</div>'+" +
            "(l.error_message?'<div class=\"log-err\">Error: '+esc(l.error_message)+'</div>':'')+" +
            "'<div class=\"log-tm\">'+relTime(l.timestamp)+'</div>'+" +
            "'</div></div>';}).join('');" +
            "document.getElementById('logList').innerHTML=html;" +
            "}).catch(function(e){console.error('Log load error:',e);});}" +

            // ── Device actions ──
            "function setPrimary(id){" +
            "fetch('/api/primary',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({deviceId:id})})" +
            ".then(function(){toast('Primary device updated','success');loadDevices();})" +
            ".catch(function(e){toast('Error: '+e.message,'error');});}" +

            "function deleteDevice(id){" +
            "if(!confirm('Remove this device?'))return;" +
            "fetch('/api/devices?id='+id,{method:'DELETE'})" +
            ".then(function(){" +
            "if(selectedDevice&&selectedDevice.device_id===id){" +
            "selectedDevice=null;currentPath='/';" +
            "document.getElementById('srchBox').disabled=true;" +
            "document.getElementById('sortSel').disabled=true;" +
            "document.getElementById('upBtn').disabled=true;" +
            "document.getElementById('fileArea').innerHTML=" +
            "'<div class=\"no-dev\"><div class=\"no-dev-ico\">&#128187;</div><p>Select a device from the sidebar</p></div>';" +
            "document.getElementById('breadcrumb').innerHTML=" +
            "'<span style=\"color:var(--faint);font-size:.82rem\">Select a device</span>';}" +
            "toast('Device removed','success');loadDevices();})" +
            ".catch(function(e){toast('Error: '+e.message,'error');});}" +

            // ── Download file ──
            "function downloadFile(path){" +
            "var url='/api/files/download?device='+encodeURIComponent(selectedDevice.device_id)+'&path='+encodeURIComponent(path);" +
            "var a=document.createElement('a');a.href=url;a.download=path.split('/').pop();" +
            "document.body.appendChild(a);a.click();document.body.removeChild(a);" +
            "setTimeout(loadLogs,1200);}" +

            // ── Add Device Modal ──
            "function showAddDeviceModal(){document.getElementById('addDevModal').classList.add('open');}" +
            "function closeAddDeviceModal(){document.getElementById('addDevModal').classList.remove('open');}" +

            "function updateDevForm(){" +
            "var t=document.getElementById('devType').value;" +
            "document.getElementById('agentFlds').style.display=t==='agent'?'block':'none';" +
            "document.getElementById('shareFlds').style.display=t==='share'?'block':'none';" +
            "document.getElementById('manualFlds').style.display=t==='manual'?'block':'none';}" +

            "function addDevice(){" +
            "var type=document.getElementById('devType').value;" +
            "var name=document.getElementById('devName').value.trim();" +
            "if(!name){toast('Please enter a device name','error');return;}" +
            "var body={deviceName:name,deviceType:type};" +
            "if(type==='agent'){" +
            "var ip=document.getElementById('ipAddr').value.trim();" +
            "var port=parseInt(document.getElementById('agentPort').value)||9877;" +
            "if(!ip){toast('Please enter an IP address','error');return;}" +
            "body.ipAddress=ip;body.port=port;" +
            "}else if(type==='share'){" +
            "var sp=document.getElementById('sharePath').value.trim();" +
            "if(!sp){toast('Please enter a share path','error');return;}" +
            "body.sharePath=sp;" +
            "}else{" +
            "var lp=document.getElementById('localPath').value.trim();" +
            "if(!lp){toast('Please enter a local path','error');return;}" +
            "body.sharePath=lp;}" +
            "fetch('/api/add-device',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)})" +
            ".then(function(r){return r.json();})" +
            ".then(function(data){" +
            "if(data.success){" +
            "closeAddDeviceModal();" +
            "document.getElementById('devName').value='';" +
            "document.getElementById('ipAddr').value='';" +
            "document.getElementById('sharePath').value='';" +
            "document.getElementById('localPath').value='';" +
            "toast('Device added!','success');loadDevices();" +
            "}else{toast('Error: '+(data.error||'Unknown error'),'error');}" +
            "})" +
            ".catch(function(e){toast('Network error: '+e.message,'error');});}" +

            // ── Upload Modal ──
            "function showUploadModal(){" +
            "if(!selectedDevice){toast('Select a device first','error');return;}" +
            "pendingFiles=[];" +
            "document.getElementById('uploadQueue').innerHTML='';" +
            "document.getElementById('upAllBtn').disabled=true;" +
            "document.getElementById('uploadModal').classList.add('open');}" +

            "function closeUploadModal(){" +
            "document.getElementById('uploadModal').classList.remove('open');" +
            "pendingFiles=[];}" +

            "function addFilesToQueue(files){" +
            "for(var i=0;i<files.length;i++)pendingFiles.push(files[i]);" +
            "renderUploadQueue();" +
            "document.getElementById('upAllBtn').disabled=pendingFiles.length===0;}" +

            "function renderUploadQueue(){" +
            "document.getElementById('uploadQueue').innerHTML=pendingFiles.map(function(f,i){" +
            "return '<div class=\"ui\" id=\"ui-'+i+'\">'+" +
            "'<span>'+fileIcon(f.name,false)+'</span>'+" +
            "'<span class=\"ui-nm\">'+esc(f.name)+'</span>'+" +
            "'<span class=\"ui-sz\">'+fmtBytes(f.size)+'</span>'+" +
            "'<span id=\"us-'+i+'\">&#9203;</span></div>';}).join('');}" +

            "async function uploadAll(){" +
            "if(!selectedDevice||!pendingFiles.length)return;" +
            "document.getElementById('upAllBtn').disabled=true;" +
            "var ok=0;" +
            "for(var i=0;i<pendingFiles.length;i++){" +
            "var statusEl=document.getElementById('us-'+i);" +
            "if(statusEl)statusEl.innerHTML='&#9203;';" +
            "var fd=new FormData();" +
            "fd.append('file',pendingFiles[i]);" +
            "fd.append('device',selectedDevice.device_id);" +
            "fd.append('path',currentPath);" +
            "try{var r=await fetch('/api/files/upload',{method:'POST',body:fd});" +
            "if(r.ok){if(statusEl)statusEl.innerHTML='&#9989;';ok++;}" +
            "else{if(statusEl)statusEl.innerHTML='&#10060;';}" +
            "}catch(e){if(statusEl)statusEl.innerHTML='&#10060;';}}" +
            "toast(ok+'/'+pendingFiles.length+' files uploaded',ok===pendingFiles.length?'success':'error');" +
            "setTimeout(function(){closeUploadModal();loadFiles();},1500);}" +

            // ── Drag-and-drop for upload zone ──
            "var dz=document.getElementById('dropZone');" +
            "dz.addEventListener('dragover',function(e){e.preventDefault();dz.classList.add('dov');});" +
            "dz.addEventListener('dragleave',function(){dz.classList.remove('dov');});" +
            "dz.addEventListener('drop',function(e){e.preventDefault();dz.classList.remove('dov');" +
            "addFilesToQueue(e.dataTransfer.files);});" +

            // ── Toast notifications ──
            "function toast(msg,type){" +
            "var c=document.getElementById('toast-c');" +
            "var el=document.createElement('div');" +
            "el.className='toast '+(type||'info');" +
            "el.textContent=msg;" +
            "c.appendChild(el);" +
            "setTimeout(function(){el.classList.add('fade-out');setTimeout(function(){if(el.parentNode)el.remove();},380);},4000);}" +

            // ── Init and polling ──
            "loadDevices();" +
            "loadLogs();" +
            "setInterval(loadDevices,8000);" +
            "setInterval(loadLogs,10000);";
    }
}
