function [motif, dimension]=getmotif(dim,res2,lmin,lmax)
if(nargin<3)
    lmin = min(res2(:,5));
    lmax = max(res2(:,5));
end
L=lmin:20:lmax;
motif=[];
dimension={};
k=1;
for i=1:length(L)-1
    idx = res2(:,5)> L(i) & res2(:,5)<L(i+1);
    tmp1=res2(idx,:);
    tmp2=dim(idx);
    dim_max=max(tmp1(:,7));
    for j=1:dim_max
        dq=tmp1(:,7)==j;
        tr=tmp1(dq,:);
        dim_tmp=tmp2(dq);
        if(~isempty(tr))
            [a, b]=sortrows(tr,6);
            motif=[motif ;a(1,:)];
            dimension{k}=dim_tmp{b(1)};
            k=k+1;
        end
    end
end