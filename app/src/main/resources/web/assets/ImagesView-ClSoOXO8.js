import{_ as rt}from"./VPagination.vuevuetypescriptsetuptruelang-CFupeDJR.js";import{_ as _t,a as pt,b as mt,c as gt,d as kt}from"./grid-view-outline-rounded-BqaCP9ek.js";import{d as ht,u as vt,s as Se,h as L,g as ft,I as bt,i as yt,ag as Te,aD as Ct,J as $t,aE as Ae,aF as wt,ah as It,K as Ee,L as Dt,M as St,c as a,a as o,m as e,t as m,O as I,x as _,w as i,p as c,e as z,q as N,aG as Tt,N as M,P as K,v as S,Q as At,aH as Et,f as Ft,C as Fe,aI as Lt,aJ as zt,aK as Mt,am as Vt,S as qt,o as n,aA as j,aL as R,E as Le,U as Bt,V as Ut,W as Nt,X as Kt,ap as Rt,ao as Gt,Z as Ht,$ as Pt,a0 as Qt,a1 as y,ae as Ot,aq as Jt,a2 as Wt,aM as Xt,a4 as Zt,a3 as jt,aN as Yt}from"./index-Dn0O6zoH.js";import{_ as xt,a as es}from"./SearchInput-aem6tEJK.js";import{_ as ts}from"./sort-rounded-BSs7VajO.js";import{_ as ss}from"./upload-rounded-q7e5EuXT.js";import{u as os}from"./search-DrT1IjAg.js";import{a as ns}from"./tags-CvPqo38V.js";import{u as ls,a as as}from"./key-events-CgPzNP0J.js";import"./search-rounded-C8Nl_ZR1.js";import"./rss-feed-rounded-Dqku7i1k.js";import"./vee-validate.esm-0lx5owW0.js";const is={class:"top-app-bar"},cs=["checked","indeterminate"],ds={class:"title"},us={key:0},rs={key:1},_s=o("md-ripple",null,null,-1),ps=o("md-ripple",null,null,-1),ms=o("md-ripple",null,null,-1),gs={class:"actions"},ks={class:"btn-icon"},hs=o("md-ripple",null,null,-1),vs=["onClick"],fs={slot:"headline"},bs=["onClick"],ys={slot:"headline"},Cs={class:"btn-icon btn-sort"},$s=o("md-ripple",null,null,-1),ws={class:"menu-items"},Is=["onClick","selected"],Ds={slot:"headline"},Ss={class:"sm"},Ts=["selected"],As=["selected"],Es=["onClick","onMouseover"],Fs=["src"],Ls=["onClick"],zs=o("md-ripple",null,null,-1),Ms=["onClick"],Vs=o("md-ripple",null,null,-1),qs=["onClick"],Bs=o("md-ripple",null,null,-1),Us=["onClick"],Ns=o("md-ripple",null,null,-1),Ks={key:1,class:"actions"},Rs=["onClick"],Gs=o("md-ripple",null,null,-1),Hs=["onClick"],Ps=o("md-ripple",null,null,-1),Qs=["onClick"],Os=o("md-ripple",null,null,-1),Js={class:"right"},Ws=["onClick","onMouseover"],Xs={class:"start"},Zs=["onClick","checked"],js=["onClick","checked"],Ys={class:"number"},xs=["src"],eo={class:"title"},to={class:"subtitle"},so=["onClick"],oo={class:"actions"},no=["onClick"],lo=o("md-ripple",null,null,-1),ao=["onClick"],io=o("md-ripple",null,null,-1),co=["onClick"],uo=o("md-ripple",null,null,-1),ro={class:"time"},_o={key:2,class:"no-data-placeholder"},C=55,Do=ht({__name:"ImagesView",setup(po){var $e;const v=vt(),{imageSortBy:G}=Se(v),f=L([]),{t:Y}=ft(),{parseQ:ze}=os(),H=bt({tagIds:[]}),x=yt(),{app:ee,urlTokenKey:P,uploads:Q}=Se(x),{input:Me,upload:Ve,uploadChanged:te}=Te(Q),{input:qe,upload:Be,uploadChanged:se}=Te(Q),{dropping:Ue,fileDragEnter:oe,fileDragLeave:ne,dropFiles:Ne}=Ct(Q),r=$t.IMAGE,le=At(),ae=le.query,O=L(parseInt((($e=ae.page)==null?void 0:$e.toString())??"1")),T=L([]),V=L([]),Ke=Ae(()=>{const t={};return V.value.forEach(l=>{t[l.id]=l}),t}),$=L(""),{addToTags:Re}=ns(r,T),{deleteItems:ie,deleteItem:ce}=wt(),{view:Ge}=Et(r),{selectedIds:g,allChecked:de,realAllChecked:D,selectRealAll:He,allCheckedAlertVisible:Pe,clearSelection:A,toggleAllChecked:ue,toggleSelect:E,total:h,checked:F,shiftEffectingIds:q,handleItemClick:re,handleMouseOver:_e,selectAll:Qe,shouldSelect:pe}=ls(f),{downloadItems:Oe}=It(P,r,A,"images.zip"),{downloadFile:me}=Rt(P),ge=t=>{const l=le.query.q;Ft(v,l?`/images?page=${t}&q=${l}`:`/images?page=${t}`)},{keyDown:ke,keyUp:he}=as(h,C,O,Qe,A,ge,()=>{ie(r,g.value,D.value,h.value,$.value)}),Je=Gt(),We=Ae(()=>f.value.map(t=>({src:j(t.fileId),name:R(t.path),duration:0,size:t.size,path:t.path,type:r,data:t})));function J(t){x.lightbox={sources:We.value,index:t,visible:!0}}const{fetch:Xe}=Ee({handle:async(t,l)=>{l?Fe(Y(l),"error"):t&&(T.value=t.tags,V.value=t.mediaBuckets)},document:Lt,variables:{type:r},appApi:!0});function ve(t){Ht(Pt,{type:r,tags:T.value,item:{key:t.id,title:t.title,size:t.size},selected:T.value.filter(l=>t.tags.some(b=>b.id===l.id))})}function Ze(t,l){G.value=l,t.close()}const{loading:B,fetch:U}=Ee({handle:async(t,l)=>{if(l)Fe(Y(l),"error");else if(t){const b=[];for(const w of t.images)b.push({...w,fileId:zt(P.value,w.path,w.id)});f.value=b,h.value=t.imageCount}},document:Mt,variables:()=>({offset:(O.value-1)*C,limit:C,query:$.value,sortBy:G.value}),appApi:!0});function je(t){return t?`/images?q=${t}`:"/images"}function W(){const t=V.value.find(l=>l.id===H.bucketId);return t?Yt(t.topItems[0]):`${ee.value.internalStoragePath}/Pictures`}function Ye(t){Ve(W()),t.close()}function xe(t){Be(W()),t.close()}function et(t){Ne(t,W(),"image")}const fe=t=>{t.type===r&&(A(),U())},be=t=>{t.type===r&&U()},ye=t=>{t.type===r&&(A(),U())},Ce=()=>{h.value--};return Dt(()=>{var t;$.value=Qt(((t=ae.q)==null?void 0:t.toString())??""),ze(H,$.value),Xe(),U(),y.on("item_tags_updated",be),y.on("items_tags_updated",fe),y.on("media_item_deleted",Ce),y.on("media_items_deleted",ye),window.addEventListener("keydown",ke),window.addEventListener("keyup",he)}),St(()=>{y.off("item_tags_updated",be),y.off("items_tags_updated",fe),y.off("media_item_deleted",Ce),y.off("media_items_deleted",ye),window.removeEventListener("keydown",ke),window.removeEventListener("keyup",he)}),(t,l)=>{const b=Ot,w=Jt,X=Wt,tt=xt,st=ss,we=Vt,ot=ts,nt=_t,lt=pt,at=es,Z=mt,Ie=gt,it=Xt,De=Zt,ct=jt,dt=kt,ut=rt,p=qt("tooltip");return n(),a(I,null,[o("div",is,[o("md-checkbox",{"touch-target":"wrapper",onChange:l[0]||(l[0]=(...s)=>e(ue)&&e(ue)(...s)),checked:e(de),indeterminate:!e(de)&&e(F)},null,40,cs),o("div",ds,[e(g).length?(n(),a("span",us,m(t.$t("x_selected",{count:e(D)?e(h).toLocaleString():e(g).length.toLocaleString()})),1)):(n(),a("span",rs,m(t.$t("page_title.images"))+" ("+m(e(h).toLocaleString())+")",1)),e(F)?(n(),a(I,{key:2},[_((n(),a("button",{class:"btn-icon",onClick:l[1]||(l[1]=i(s=>e(ie)(e(r),e(g),e(D),e(h),$.value),["stop"]))},[_s,c(b)])),[[p,t.$t("delete")]]),_((n(),a("button",{class:"btn-icon",onClick:l[2]||(l[2]=i(s=>e(Oe)(e(D),e(g),$.value),["stop"]))},[ps,c(w)])),[[p,t.$t("download")]]),_((n(),a("button",{class:"btn-icon",onClick:l[3]||(l[3]=i(s=>e(Re)(e(g),e(D),$.value),["stop"]))},[ms,c(X)])),[[p,t.$t("add_to_tags")]])],64)):z("",!0)]),o("div",gs,[c(tt,{filter:H,tags:T.value,buckets:V.value,"get-url":je},null,8,["filter","tags","buckets"]),c(we,null,{content:N(s=>[o("md-menu-item",{onClick:i(d=>Ye(s),["stop"])},[o("div",fs,m(t.$t("upload_files")),1)],8,vs),o("md-menu-item",{onClick:i(d=>xe(s),["stop"])},[o("div",ys,m(t.$t("upload_folder")),1)],8,bs)]),default:N(()=>[_((n(),a("button",ks,[hs,c(st)])),[[p,t.$t("upload")]])]),_:1}),c(we,null,{content:N(s=>[o("div",ws,[(n(!0),a(I,null,K(e(Je),d=>(n(),a("md-menu-item",{onClick:u=>Ze(s,d.value),key:d.value,selected:d.value===e(G)},[o("div",Ds,m(t.$t(d.label)),1)],8,Is))),128))])]),default:N(()=>[_((n(),a("button",Cs,[$s,c(ot)])),[[p,t.$t("sort")]])]),_:1}),o("md-outlined-segmented-button-set",Ss,[o("md-outlined-segmented-button",{"data-value":"grid","no-checkmark":"",selected:!e(v).imagesCardView,onClick:l[4]||(l[4]=s=>e(v).imagesCardView=!1)},[c(nt,{slot:"icon"})],8,Ts),o("md-outlined-segmented-button",{"data-value":"card","no-checkmark":"",selected:e(v).imagesCardView,onClick:l[5]||(l[5]=s=>e(v).imagesCardView=!0)},[c(lt,{slot:"icon"})],8,As)])])]),c(at,{limit:C,total:e(h),"all-checked-alert-visible":e(Pe),"real-all-checked":e(D),"select-real-all":e(He),"clear-selection":e(A)},null,8,["total","all-checked-alert-visible","real-all-checked","select-real-all","clear-selection"]),o("div",{class:"scroll-content",onDragover:l[9]||(l[9]=i((...s)=>e(oe)&&e(oe)(...s),["stop","prevent"]))},[_(o("div",{class:"drag-mask",onDrop:i(et,["stop","prevent"]),onDragleave:l[6]||(l[6]=i((...s)=>e(ne)&&e(ne)(...s),["stop","prevent"]))},m(t.$t("release_to_send_files")),545),[[Tt,e(Ue)]]),e(v).imagesCardView?(n(),a("div",{key:1,class:M(["media-list",{"select-mode":e(F)}])},[(n(!0),a(I,null,K(f.value,(s,d)=>{var u;return n(),a("section",{class:M(["media-item selectable-card",{selected:e(g).includes(s.id),selecting:e(q).includes(s.id)}]),key:s.id,onClick:i(k=>e(re)(k,s,d,J),["stop"]),onMouseover:k=>e(_e)(k,d)},[o("div",Xs,[e(q).includes(s.id)?(n(),a("md-checkbox",{key:0,class:"checkbox","touch-target":"wrapper",onClick:i(k=>e(E)(k,s,d),["stop"]),checked:e(pe)},null,8,Zs)):(n(),a("md-checkbox",{key:1,class:"checkbox","touch-target":"wrapper",onClick:i(k=>e(E)(k,s,d),["stop"]),checked:e(g).includes(s.id)},null,8,js)),o("span",Ys,[c(ct,{id:d+1,raw:s},null,8,["id","raw"])])]),o("img",{class:"image",src:e(j)(s.fileId,"&w=200&h=200"),onerror:"this.src='/broken-image.png'"},null,8,xs),o("div",eo,m(e(R)(s.path)),1),o("div",to,[o("span",null,m(e(Le)(s.size)),1),o("a",{onClick:i(k=>e(Ge)(e(v),s.bucketId),["stop","prevent"])},m((u=Ke.value[s.bucketId])==null?void 0:u.name),9,so),c(De,{tags:s.tags,type:e(r),"only-links":!0},null,8,["tags","type"])]),o("div",oo,[_((n(),a("button",{class:"btn-icon sm",onClick:i(k=>e(ce)(e(r),s),["stop"])},[lo,c(b)],8,no)),[[p,t.$t("delete")]]),_((n(),a("button",{class:"btn-icon sm",onClick:i(k=>e(me)(s.path,e(R)(s.path).replace(" ","-")),["stop"])},[io,c(w)],8,ao)),[[p,t.$t("download")]]),_((n(),a("button",{class:"btn-icon sm",onClick:i(k=>ve(s),["stop"])},[uo,c(X)],8,co)),[[p,t.$t("add_to_tags")]])]),o("div",ro,[_((n(),a("span",null,[Ut(m(e(Nt)(s.createdAt)),1)])),[[p,e(Bt)(s.createdAt)]])])],42,Ws)}),128)),e(B)&&f.value.length===0?(n(),S(dt,{key:0,limit:C})):z("",!0)],2)):(n(),a("div",{key:0,class:M(["media-grid",{"select-mode":e(F)}])},[(n(!0),a(I,null,K(f.value,(s,d)=>(n(),a("section",{class:M(["media-item",{selected:e(g).includes(s.id),selecting:e(q).includes(s.id)}]),key:s.id,onClick:i(u=>e(re)(u,s,d,J),["stop"]),onMouseover:u=>e(_e)(u,d)},[o("img",{class:"image-thumb image",src:e(j)(s.fileId,"&w=200&h=200"),onerror:"this.src='/broken-image.png'"},null,8,Fs),e(q).includes(s.id)?(n(),a("button",{key:0,class:"btn-icon btn-checkbox",onClick:i(u=>e(E)(u,s,d),["stop"])},[zs,e(pe)?(n(),S(Z,{key:0})):(n(),S(Ie,{key:1}))],8,Ls)):e(g).includes(s.id)?(n(),a("button",{key:1,class:"btn-icon btn-checkbox",onClick:i(u=>e(E)(u,s,d),["stop"])},[Vs,c(Z)],8,Ms)):(n(),a(I,{key:2},[o("button",{class:"btn-icon btn-checkbox",onClick:i(u=>e(E)(u,s,d),["stop"])},[Bs,e(g).includes(s.id)?(n(),S(Z,{key:0})):(n(),S(Ie,{key:1}))],8,qs),e(F)?_((n(),a("button",{key:0,class:"btn-icon btn-zoom sm",onClick:i(u=>J(d),["stop"])},[Ns,c(it)],8,Us)),[[p,t.$t("open")]]):(n(),a("div",Ks,[_((n(),a("button",{class:"btn-icon sm",onClick:i(u=>e(ce)(e(r),s),["stop"])},[Gs,c(b)],8,Rs)),[[p,t.$t("delete")]]),_((n(),a("button",{class:"btn-icon sm",onClick:i(u=>e(me)(s.path,e(R)(s.path).replace(" ","-")),["stop"])},[Ps,c(w)],8,Hs)),[[p,t.$t("download")]]),_((n(),a("button",{class:"btn-icon sm",onClick:i(u=>ve(s),["stop"])},[Os,c(X)],8,Qs)),[[p,t.$t("add_to_tags")]])])),o("div",{class:M(["info",{"has-tags":s.tags.length>0}])},[c(De,{tags:s.tags,type:e(r)},null,8,["tags","type"]),o("span",Js,m(e(Le)(s.size)),1)],2)],64))],42,Es))),128)),e(B)&&f.value.length===0?(n(),a(I,{key:0},K(C,s=>o("section",{class:"skeleton-image media-item",key:s})),64)):z("",!0)],2)),!e(B)&&f.value.length===0?(n(),a("div",_o,m(t.$t(e(Kt)(e(B),e(ee).permissions,"WRITE_EXTERNAL_STORAGE"))),1)):z("",!0),e(h)>C?(n(),S(ut,{key:3,page:O.value,go:ge,total:e(h),limit:C},null,8,["page","total"])):z("",!0),o("input",{ref_key:"fileInput",ref:Me,style:{display:"none"},type:"file",accept:"image/*",multiple:"",onChange:l[7]||(l[7]=(...s)=>e(te)&&e(te)(...s))},null,544),o("input",{ref_key:"dirFileInput",ref:qe,style:{display:"none"},type:"file",accept:"image/*",multiple:"",webkitdirectory:"",mozdirectory:"",directory:"",onChange:l[8]||(l[8]=(...s)=>e(se)&&e(se)(...s))},null,544)],32)],64)}}});export{Do as default};