import{_ as Se}from"./VPagination.vuevuetypescriptsetuptruelang-CFupeDJR.js";import{_ as Le}from"./call-outline-rounded-BpLo2E1r.js";import{d as Te,u as Ie,s as qe,i as Ae,h as I,g as De,I as xe,J as Ve,K as Ee,j as Me,L as Ne,M as Be,c as n,a,m as t,t as d,O as q,x as h,w as _,p as r,e as A,N as X,P as Y,v as Ge,Q as Qe,a1 as g,a9 as Ue,f as Ke,C as Re,aa as ze,B as He,S as Oe,o,ab as Pe,U as je,V as Fe,W as We,X as Je,F as Xe,G as Ye,Y as Ze,Z,$ as et,ac as tt,ad as st,a0 as at,ae as lt,a2 as ot,a3 as nt,a4 as ct,H as it}from"./index-Dn0O6zoH.js";import{_ as dt,a as rt}from"./SearchInput-aem6tEJK.js";import{b as ut,u as _t,a as pt}from"./key-events-CgPzNP0J.js";import{u as vt}from"./search-DrT1IjAg.js";import{u as mt,a as ht}from"./tags-CvPqo38V.js";import"./search-rounded-C8Nl_ZR1.js";import"./rss-feed-rounded-Dqku7i1k.js";import"./vee-validate.esm-0lx5owW0.js";const b=$=>(Xe("data-v-c8b1bdcc"),$=$(),Ye(),$),gt={class:"top-app-bar"},kt=["checked","indeterminate"],bt={class:"title"},ft={key:0},yt={key:1},Ct=b(()=>a("md-ripple",null,null,-1)),$t=b(()=>a("md-ripple",null,null,-1)),wt={class:"actions"},St={class:"scroll-content"},Lt=["onClick","onMouseover"],Tt={class:"start"},It=["onClick","checked"],qt=["onClick","checked"],At={class:"number"},Dt={class:"title"},xt={class:"subtitle"},Vt={class:"actions"},Et=["onClick"],Mt=b(()=>a("md-ripple",null,null,-1)),Nt={key:0,indeterminate:"",class:"spinner-sm"},Bt=["onClick"],Gt=b(()=>a("md-ripple",null,null,-1)),Qt=["onClick"],Ut=b(()=>a("md-ripple",null,null,-1)),Kt={class:"geo"},Rt={class:"time"},zt={class:"start"},Ht=b(()=>a("div",{class:"checkbox"},[a("div",{class:"skeleton-checkbox"})],-1)),Ot={class:"number"},Pt=Ze('<div class="title" data-v-c8b1bdcc><div class="skeleton-text skeleton-title" data-v-c8b1bdcc></div></div><div class="subtitle" data-v-c8b1bdcc><div class="skeleton-text skeleton-subtitle" data-v-c8b1bdcc></div></div><div class="actions" data-v-c8b1bdcc><div class="skeleton-text skeleton-actions" data-v-c8b1bdcc></div></div><div class="geo" data-v-c8b1bdcc><div class="skeleton-text skeleton-geo" data-v-c8b1bdcc></div></div><div class="time" data-v-c8b1bdcc><div class="skeleton-text skeleton-time" data-v-c8b1bdcc></div></div>',5),jt={key:0,class:"no-data-placeholder"},k=50,Ft=Te({__name:"CallsView",setup($){var W;const ee=Ie(),{app:te}=qe(Ae()),f=I([]),{t:D}=De(),{parseQ:se}=vt(),M=xe({tagIds:[]}),u=Ve.CALL,N=Qe(),B=N.query,x=I(parseInt(((W=B.page)==null?void 0:W.toString())??"1")),v=I(""),{tags:w,fetch:ae}=mt(u),{addToTags:le}=ht(u,w),{deleteItems:G}=ut(Ue,()=>{S(),L(),g.emit("refetch_tags",u)}),{selectedIds:p,allChecked:Q,realAllChecked:y,selectRealAll:oe,allCheckedAlertVisible:ne,clearSelection:S,toggleAllChecked:U,toggleSelect:K,total:c,checked:V,shiftEffectingIds:R,handleItemClick:ce,handleMouseOver:ie,selectAll:de,shouldSelect:re}=_t(f),z=e=>{const l=N.query.q;Ke(ee,l?`/calls?page=${e}&q=${l}`:`/calls?page=${e}`)},{keyDown:H,keyUp:O}=pt(c,k,x,de,S,z,()=>{G(p.value,y.value,c.value,v.value)}),{loading:E,fetch:L}=Ee({handle:(e,l)=>{l?Re(D(l),"error"):e&&(f.value=e.calls,c.value=e.callCount)},document:ze,variables:()=>({offset:(x.value-1)*k,limit:k,query:v.value}),appApi:!0}),ue=["1","2","3"].map(e=>({id:e,name:D("call_type."+e)}));function _e(e){Z(et,{type:u,tags:w.value,item:{key:e.id,title:"",size:0},selected:w.value.filter(l=>e.tags.some(T=>T.id===l.id))})}function pe(e){return e?`/calls?q=${e}`:"/calls"}const P=e=>{e.type===u&&(S(),L())},j=e=>{e.type===u&&L()};function ve(e){if(!e)return"";const l=[];return e.isp&&l.push(D("phone_isp_type."+e.isp)),e.city===e.province?l.push(e.city):l.push(`${e.province}${e.city}`),l.join(", ")}const F=I(""),{mutate:me,loading:he}=Me({document:He,appApi:!0});function ge(e){F.value=e.id,me({number:e.number})}function ke(e){Z(st,{id:e.id,name:e.number,gql:tt`
      mutation DeleteCall($query: String!) {
        deleteCalls(query: $query)
      }
    `,variables:()=>({query:`ids:${e.id}`}),appApi:!0,typeName:"Call",done:()=>{c.value--,e.tags.length&&g.emit("refetch_tags",u)}})}return Ne(()=>{var e;v.value=at(((e=B.q)==null?void 0:e.toString())??""),se(M,v.value),ae(),L(),g.on("item_tags_updated",j),g.on("items_tags_updated",P),window.addEventListener("keydown",H),window.addEventListener("keyup",O)}),Be(()=>{g.off("item_tags_updated",j),g.off("items_tags_updated",P),window.removeEventListener("keydown",H),window.removeEventListener("keyup",O)}),(e,l)=>{const T=lt,J=ot,be=dt,fe=rt,ye=nt,Ce=ct,$e=Le,we=Se,m=Oe("tooltip");return o(),n(q,null,[a("div",gt,[a("md-checkbox",{"touch-target":"wrapper",onChange:l[0]||(l[0]=(...s)=>t(U)&&t(U)(...s)),checked:t(Q),indeterminate:!t(Q)&&t(V)},null,40,kt),a("div",bt,[t(p).length?(o(),n("span",ft,d(e.$t("x_selected",{count:t(y)?t(c).toLocaleString():t(p).length.toLocaleString()})),1)):(o(),n("span",yt,d(e.$t("page_title.calls"))+" ("+d(t(c).toLocaleString())+")",1)),t(V)?(o(),n(q,{key:2},[h((o(),n("button",{class:"btn-icon",onClick:l[1]||(l[1]=_(s=>t(G)(t(p),t(y),t(c),v.value),["stop"]))},[Ct,r(T)])),[[m,e.$t("delete")]]),h((o(),n("button",{class:"btn-icon",onClick:l[2]||(l[2]=_(s=>t(le)(t(p),t(y),v.value),["stop"]))},[$t,r(J)])),[[m,e.$t("add_to_tags")]])],64)):A("",!0)]),a("div",wt,[r(be,{filter:M,tags:t(w),types:t(ue),"get-url":pe},null,8,["filter","tags","types"])])]),r(fe,{limit:k,total:t(c),"all-checked-alert-visible":t(ne),"real-all-checked":t(y),"select-real-all":t(oe),"clear-selection":t(S)},null,8,["total","all-checked-alert-visible","real-all-checked","select-real-all","clear-selection"]),a("div",St,[a("div",{class:X(["call-list",{"select-mode":t(V)}])},[(o(!0),n(q,null,Y(f.value,(s,C)=>(o(),n("section",{class:X(["call-item selectable-card",{selected:t(p).includes(s.id),selecting:t(R).includes(s.id)}]),key:s.id,onClick:_(i=>t(ce)(i,s,C,()=>{}),["stop"]),onMouseover:i=>t(ie)(i,C)},[a("div",Tt,[t(R).includes(s.id)?(o(),n("md-checkbox",{key:0,class:"checkbox","touch-target":"wrapper",onClick:_(i=>t(K)(i,s,C),["stop"]),checked:t(re)},null,8,It)):(o(),n("md-checkbox",{key:1,class:"checkbox","touch-target":"wrapper",onClick:_(i=>t(K)(i,s,C),["stop"]),checked:t(p).includes(s.id)},null,8,qt)),a("span",At,[r(ye,{id:C+1,raw:s},null,8,["id","raw"])])]),a("div",Dt,d(s.name?s.name+" "+s.number:s.number),1),a("div",xt,[a("span",null,d(t(Pe)(s.duration)),1),a("span",null,d(e.$t("call_type."+s.type)),1),r(Ce,{tags:s.tags,type:t(u),"only-links":!0},null,8,["tags","type"])]),a("div",Vt,[h((o(),n("button",{class:"btn-icon sm",onClick:_(i=>ke(s),["stop"])},[Mt,r(T)],8,Et)),[[m,e.$t("delete")]]),t(he)&&F.value===s.id?(o(),n("md-circular-progress",Nt)):h((o(),n("button",{key:1,class:"btn-icon sm",onClick:_(i=>ge(s),["stop"])},[Gt,r($e)],8,Bt)),[[m,e.$t("make_a_phone_call")]]),h((o(),n("button",{class:"btn-icon sm",onClick:_(i=>_e(s),["stop"])},[Ut,r(J)],8,Qt)),[[m,e.$t("add_to_tags")]])]),a("div",Kt,d(ve(s.geo)),1),a("div",Rt,[h((o(),n("span",null,[Fe(d(t(We)(s.startedAt)),1)])),[[m,t(je)(s.startedAt)]])])],42,Lt))),128)),t(E)&&f.value.length===0?(o(),n(q,{key:0},Y(20,s=>a("section",{class:"call-item selectable-card-skeleton",key:s},[a("div",zt,[Ht,a("span",Ot,d(s),1)]),Pt])),64)):A("",!0)],2),!t(E)&&f.value.length===0?(o(),n("div",jt,d(e.$t(t(Je)(t(E),t(te).permissions,"WRITE_CALL_LOG"))),1)):A("",!0),t(c)>k?(o(),Ge(we,{key:1,page:x.value,go:z,total:t(c),limit:k},null,8,["page","total"])):A("",!0)])],64)}}}),os=it(Ft,[["__scopeId","data-v-c8b1bdcc"]]);export{os as default};