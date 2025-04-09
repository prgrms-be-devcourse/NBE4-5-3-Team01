import MembershipTable from "./MembershipTable";

export default function AdminMembershipPage() {
    return (
        <div className="p-8 bg-[#fefbff] min-h-screen">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">📋 멤버십 관리</h1>
            <p className="text-gray-600 mb-10">
                전체 유저의 멤버십 정보를 조회하고 수정할 수 있습니다.
            </p>
            <MembershipTable />
        </div>
    );
}
