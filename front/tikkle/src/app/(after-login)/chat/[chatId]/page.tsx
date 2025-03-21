"use client";

import { useFetchChatroomById } from "@/hooks/chat/useFetchChatroomById";
import Loading from "@/components/loading/Loading";
import { useState, useEffect, useRef } from "react";
import { usePathname } from "next/navigation";
import Image from "next/image";
import Button from "@/components/button/Button";
import Badge from "@/components/badge/Badge";
import Link from "next/link";
import ChatList from "@/components/chat/ChatList";
import PromiseDropdown from "@/components/drop-down/PromiseDropdown";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { Chat } from "@/types/chat";
import { useFetchAppointmentByRoomId } from "@/hooks/appointment/useFetchAppointmentByRoomId";
import { useDeleteAppointmentById } from "@/hooks/appointment/useDeleteAppointmentById";
import { useMypageStore } from "@/store/mypageStore";
import { useFetchMypageMember } from "@/hooks";
import ReviewCard from "@/components/card/ReviewCard";

export default function ChatId() {
  const pathname = usePathname();

  // URL에서 roomId 추출 (예: '/chat/31000000-0000-0000-0000-000000000000')
  const roomId = pathname.split("/").pop()!; // 경로의 마지막 부분이 roomId, Non-null assertion 사용

  const member = useMypageStore((state) => state.member); // zustand에서 현재 member 상태 가져오기
  const setMember = useMypageStore((state) => state.setMember); // zustand에서 setMember 가져오기
  const [fetchData, setFetchData] = useState(false); // API 호출 여부를 제어하기 위한 로컬 상태

  useEffect(() => {
    if (!member) {
      setFetchData(true); // member가 없으면 데이터를 받아오도록 설정
    }
  }, [member]);

  const { data: memberData, isLoading, error } = useFetchMypageMember(); // 인자 없이 호출

  useEffect(() => {
    if (memberData && !member && fetchData) {
      setMember(memberData); // member 상태가 없을 때만 zustand에 저장
      setFetchData(false); // 데이터를 받아온 후 다시 API 호출을 막기 위해 설정
    }
  }, [memberData, member, setMember, fetchData]);

  console.log("zustand member state:", member);

  const {
    data: chatroomData,
    error: chatroomError,
    isLoading: isChatroomLoading,
    refetch: refetchChatroom,
  } = useFetchChatroomById(roomId!);

  console.log("fetched chatroom detail: ", chatroomData);

  const getBadgeColor = (status: string) => {
    switch (status) {
      case "진행전":
        return "red";
      case "진행중":
        return "yellow";
      case "완료됨":
        return "gray";
      default:
        return "red";
    }
  };

  /////////////////// 채팅 로직
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      handleSendMessage();
    }
  };

  const [inputValue, setInputValue] = useState(""); // 메시지 입력 값
  const [messages, setMessages] = useState<Chat[]>([]); // 메시지를 Chat[] 형식으로 관리
  const stompClientRef = useRef<Client | null>(null);

  useEffect(() => {
    // const socket = new SockJS("http://localhost:8080/ws");
    const socket = new SockJS("https://j11a501.p.ssafy.io/ws");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000, // 5초 후에 재연결 시도
      onConnect: () => {
        console.log("WebSocket 연결 성공 및 STOMP 연결 확립");
        stompClientRef.current = stompClient;
      },
      onStompError: (frame) => {
        console.error("STOMP 에러:", frame.headers["message"]);
      },
    });

    // WebSocket 연결이 성공했을 때
    stompClient.onConnect = () => {
      console.log("WebSocket 연결됨");

      stompClient.subscribe(`/topic/chatroom.${roomId}`, (message) => {
        const receivedMessage = JSON.parse(message.body);
        const newChat = {
          content: receivedMessage.content,
          timestamp: receivedMessage.timestamp,
          senderId: receivedMessage.senderId,
        };

        setMessages((prevMessages) => [...prevMessages, newChat]);
        console.log("수신된 메시지:", newChat); // 수신된 메시지 로그
      });
    };

    // WebSocket 연결이 종료되었을 때
    stompClient.onDisconnect = () => {
      console.log("WebSocket 연결이 종료됨");
    };

    stompClient.activate();
    stompClientRef.current = stompClient;

    return () => {
      stompClient.deactivate();
    };
  }, [roomId]);

  const handleSendMessage = () => {
    if (stompClientRef.current && inputValue.trim() !== "") {
      const chatMessage = {
        chatroomId: roomId,
        senderId: member?.id,
        content: inputValue,
      };

      const sendMessage = {
        destination: "/app/sendMessage",
        body: JSON.stringify(chatMessage),
      };
      console.log("chatMessage는 말이죠 : ", chatMessage);
      console.log(
        "chatroomId의 타입은 말이죠 : ",
        typeof chatMessage.chatroomId,
      );

      stompClientRef.current.publish(sendMessage);
      console.log("메시지 전송:", sendMessage); // 전송한 메시지 로그
      setInputValue("");
    }
  };

  const combinedMessages = [...(chatroomData?.chats || []), ...messages];

  ////////////////// 스크롤 로직
  // Scroll container를 참조할 ref 생성
  const scrollRef = useRef<HTMLDivElement | null>(null);

  // 컴포넌트가 렌더링되거나 messages가 업데이트될 때마다 스크롤을 아래로
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [combinedMessages]);

  ////////////////// 약속잡기 로직
  // 약속잡기 버튼을 작성자에게만 렌더링
  const [showPromiseDropdown, setShowPromiseDropdown] = useState(false); // 약속 잡기 드롭다운 상태 관리
  const handleTogglePromiseDropdown = () => {
    setShowPromiseDropdown((prevState) => !prevState);
  };

  ////////////////// 약속조회 로직
  const {
    data: appointmentData,
    error: appointmentError,
    isLoading: isAppointmentLoading,
    refetch: refetchAppointment,
  } = useFetchAppointmentByRoomId(roomId);

  ////////////////// 약속삭제 로직
  const deleteAppointmentMutation = useDeleteAppointmentById();

  const handleDeleteAppointment = (appointmentId: string) => {
    if (window.confirm("정말로 약속을 취소하시겠습니까?")) {
      deleteAppointmentMutation.mutate(appointmentId, {
        onSuccess: () => {
          refetchAppointment();
        },
        onError: (error) => {
          alert(error.response.data.message);
          console.error("Error deleting appointment:", error);
        },
      });
    }
  };

  // 거래 완료하기 로직
  const [showReviewModal, setShowReviewModal] = useState(false); // ReviewCard 모달 표시 상태 관리

  const handleReviewModalToggle = () => {
    setShowReviewModal((prev) => !prev); // 모달 표시 상태 토글
  };

  // 로딩 중일 때 보여줄 내용
  if (isChatroomLoading) {
    return (
      <>
        <Loading />
      </>
    );
  }

  // 에러 시 보여줄 내용
  if (chatroomError) {
    return <p>Error: {chatroomError.message}</p>;
  }

  return (
    <>
      {/* 채팅 헤더 */}
      <div className="item flex items-start justify-between self-stretch px-10 pb-0 pt-10">
        <div className="flex items-center gap-10">
          <Image
            src={
              chatroomData?.partnerImage
                ? `data:image/png;base64,${chatroomData.partnerImage}`
                : "/profile.png"
            }
            alt={`${chatroomData?.partnerName} profile`}
            width={41}
            height={41}
            className="rounded-round"
          />
          <div className="flex py-10 text-28 font-bold text-teal-900">
            {chatroomData?.partnerName}님과의 대화
          </div>
        </div>

        {/* 상태가 완료됨 일 경우 아무것도 렌더링하지 않음 */}
        {chatroomData?.status !== "완료됨" && (
          <div className="relative flex h-full items-center justify-center">
            {/* 약속 관련 로직 */}
            {isAppointmentLoading ? (
              // 로딩 중일 때
              <div className="flex h-full w-full items-center justify-center">
                <Loading />
              </div>
            ) : appointmentError ? (
              // 에러가 있을 때
              <p>Error: {appointmentError.message}</p>
            ) : appointmentData?.appointmentId &&
              appointmentData?.appointmentTime ? (
              // 약속이 있을 때 (appointmentId와 appointmentTime이 null이 아닐 때)
              <div className="flex items-center justify-end gap-6 self-stretch p-10">
                <div>
                  {new Date(appointmentData?.appointmentTime).toLocaleString(
                    "ko-KR",
                    {
                      month: "long", // 월을 '9월'과 같이 표시
                      day: "numeric", // 일을 숫자로 표시
                      hour: "2-digit", // 시간을 두 자리로 표시
                      minute: "2-digit", // 분을 두 자리로 표시
                      hour12: false, // 24시간 형식을 사용
                    },
                  )}
                  에
                </div>
                <div className="font-bold text-teal700">
                  {appointmentData?.timeQnt}시간
                </div>
                <div>약속</div>
                {/* 약속취소 , 거래완료 버튼을 작성자에게만 렌더링 */}
                {chatroomData?.writerId === member?.id && (
                  <>
                    <Button
                      size="s"
                      variant="primary"
                      design="fill"
                      main="약속취소"
                      onClick={() =>
                        handleDeleteAppointment(appointmentData.appointmentId)
                      }
                    />
                    <Button
                      size="s"
                      variant="primary"
                      design="outline"
                      main="거래 완료하기"
                      onClick={handleReviewModalToggle}
                    />
                    {/* ReviewCard 모달 */}
                    {showReviewModal && (
                      <div className="mt-12">
                        <ReviewCard
                          chatroomId={roomId}
                          refetchChatroom={refetchChatroom}
                        />
                      </div>
                    )}
                  </>
                )}
              </div>
            ) : (
              // 약속이 없을 때 '약속잡기' 버튼을 작성자에게만 표시
              chatroomData?.writerId === member?.id && (
                <div>
                  <Button
                    size="m"
                    variant="primary"
                    design="fill"
                    main="약속잡기"
                    onClick={handleTogglePromiseDropdown}
                  />
                  {/* PromiseDropdown 버튼 아래 표시 */}
                  {showPromiseDropdown && (
                    <div className="mt-12">
                      <PromiseDropdown
                        roomId={roomId}
                        refetchAppointment={refetchAppointment}
                        refetchChatroom={refetchChatroom}
                      />
                    </div>
                  )}
                </div>
              )
            )}
          </div>
        )}
      </div>
      <div className="flex items-center gap-6 self-stretch border-b border-b-coolGray300 p-10">
        <Badge size="l" color={getBadgeColor(chatroomData!.status)}>
          {chatroomData?.status}
        </Badge>
        {chatroomData?.deleted ? (
          <div className="text-15 text-coolGray500">
            {chatroomData?.boardTitle} (삭제된 게시글)
          </div>
        ) : (
          <Link href={`/board/${chatroomData?.boardId}`}>
            <div className="text-15">{chatroomData?.boardTitle}</div>
          </Link>
        )}
      </div>

      {/* 채팅 내용 */}
      <div
        ref={scrollRef}
        className="scrollbar-custom flex flex-1 flex-col self-stretch overflow-y-auto"
      >
        {combinedMessages.length > 0 ? (
          combinedMessages.map((chat, index) => (
            <ChatList
              key={index}
              content={chat.content}
              createdAt={chat.timestamp}
              senderId={chat.senderId}
              isMine={chat.senderId === member?.id}
            />
          ))
        ) : (
          <div className="flex h-full items-center justify-center">
            <p className="text-center text-warmGray500">
              아직 메시지가 없습니다.
            </p>
          </div>
        )}
      </div>

      {/* 채팅 인풋 */}
      <div className="h-42 flex items-center justify-center self-stretch rounded-10 border border-coolGray400 p-10">
        <input
          type="text"
          placeholder="내용을 입력하세요."
          value={inputValue}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          className="flex-1 appearance-none bg-coolGray100 text-17 placeholder-warmGray300 focus:outline-none"
        />
      </div>
    </>
  );
}
